package matchpictures.com.matchpictures.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import matchpictures.com.matchpictures.Constant;
import matchpictures.com.matchpictures.PhotoAdapter;
import matchpictures.com.matchpictures.R;
import matchpictures.com.matchpictures.apiService.APIService;
import matchpictures.com.matchpictures.apiService.GetAvailableSizesService;
import matchpictures.com.matchpictures.model.FlickrAPIResponse;
import matchpictures.com.matchpictures.model.FlickrSizeAPIResponse;
import matchpictures.com.matchpictures.model.PhotoItem;
import matchpictures.com.matchpictures.model.Size;
import matchpictures.com.matchpictures.presenter.PhotoPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
    implements IPhotoView, PhotoAdapter.PhotoItemClickListener {
    private PhotoPresenter photoPresenter;
    private RecyclerView recyclerView;
    private TextView tvTotalFlips;
    private TextView tvFinishGame;
    private TextView tvErrorLoading;
    private ProgressBar progressBar;
    private PhotoAdapter photoAdapter;
    private static List<PhotoItem> listOfPhotoItems = new ArrayList<>();
    private HashMap<String, PhotoItem> photoItemsMap = new HashMap<>();
    private View firstClickedView, clickedView;
    private Button btnReset;
    private int totalClicks = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoAdapter = new PhotoAdapter(this, this);
        recyclerView = findViewById(R.id.rc_photos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Constant.NO_OF_COLUMNS));

        tvTotalFlips = findViewById(R.id.tv_total_flip);
        tvTotalFlips.setText(String.format(getString(R.string.no_of_flips), String.valueOf(0)));

        tvFinishGame = findViewById(R.id.tv_done_msg);
        tvErrorLoading = findViewById(R.id.tv_no_data);
        progressBar = findViewById(R.id.pb_loading);

        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(view -> photoPresenter.reset());
    }

    @Override protected void onStart() {
        super.onStart();
        initializeGame();
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override public void updateFlip(int totalFlips) {
        tvTotalFlips.setText(
            String.format(getString(R.string.no_of_flips), String.valueOf(totalFlips)));
    }

    @Override public void flipOver() {
        ImageView ivCover = clickedView.findViewById(R.id.iv_photo_cover);
        ivCover.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override public void flipBack() {
        ImageView ivFirstPhotoCover = firstClickedView.findViewById(R.id.iv_photo_cover);
        ivFirstPhotoCover.setBackgroundColor(getResources().getColor(R.color.gray));
        ImageView ivSecondPhotoCover = clickedView.findViewById(R.id.iv_photo_cover);
        ivSecondPhotoCover.setBackgroundColor(getResources().getColor(R.color.gray));
        firstClickedView = null;
        totalClicks = 0;
    }

    @Override public void leaveOpen() {
        firstClickedView = null;
        totalClicks = 0;
    }

    @Override public void finishGame() {
        tvFinishGame.setVisibility(View.VISIBLE);
    }

    @Override public void resetView() {
        tvTotalFlips.setText(String.format(getString(R.string.no_of_flips), String.valueOf(0)));
        resetGame();
        Collections.shuffle(listOfPhotoItems);
        tvFinishGame.setVisibility(View.GONE);
        photoPresenter.setPhotoItems(listOfPhotoItems);
        photoAdapter.setListOfPhotoItemsAfterReset(listOfPhotoItems);
    }

    private void initializeGame() {
        progressBar.setVisibility(View.VISIBLE);
        setVisibility(View.GONE);
        preparePhotos();
    }

    private void setVisibility(int value) {
        tvTotalFlips.setVisibility(value);
        btnReset.setVisibility(value);
        recyclerView.setVisibility(value);
    }

    private void showErrorLoading() {
        progressBar.setVisibility(View.GONE);
        tvErrorLoading.setVisibility(View.VISIBLE);
    }

    private void successLoading() {
        setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvErrorLoading.setVisibility(View.GONE);
    }

    private void resetGame() {
        for (PhotoItem photoItem : listOfPhotoItems) {
            photoItem.setOpen(false);
        }
    }

    private void preparePhotos() {
        Call<FlickrAPIResponse> response = APIService.provideGetRecentPhotosService()
            .getRecentPhotos(Constant.API_KEY, Constant.GET_PHOTOS_METHOD, Constant.FORMAT,
                Constant.NO_OF_ITEMS_PER_PAGE, Constant.NO_OF_PAGE);
        response.enqueue(new Callback<FlickrAPIResponse>() {
            @Override public void onResponse(
                Call<FlickrAPIResponse> call, Response<FlickrAPIResponse> response) {
                if (response.body() != null && response.body().getPhotos() != null) {
                    getAvailableSizes(response.body().getPhotos().getPhoto());
                }
            }

            @Override public void onFailure(Call<FlickrAPIResponse> call, Throwable t) {
                showErrorLoading();
            }
        });
    }

    @SuppressLint("CheckResult") private void getAvailableSizes(List<PhotoItem> photos) {
        List<Observable<?>> requests = new ArrayList<>();
        GetAvailableSizesService getAvailableSizesService = APIService.provideAvailableSize();
        for (PhotoItem photoItem : photos) {
            photoItemsMap.put(photoItem.getId(), photoItem);
            requests.add(getAvailableSizesService.getAvailableSizes(Constant.API_KEY,
                Constant.GET_SIZES_METHOD, Constant.FORMAT, photoItem.getId()));
        }
        // Zip all requests with the Function, which will receive the results.
        Observable.zip(requests, objects -> objects)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(objects -> {
                successLoading();
                MainActivity.this.preparePhotoItemsWithUrls(objects);
            }, e -> showErrorLoading());
    }

    private void preparePhotoItemsWithUrls(Object[] flickrSizeAPIResponses) {
        //requested 10 photos in the case that there is no available square size for photos, but only need 8 photos
        int totalPhotos = Constant.NO_OF_COLUMNS * Constant.NO_OF_ROWS;

        for (int i = 0; i < flickrSizeAPIResponses.length; i++) {
            FlickrSizeAPIResponse response = (FlickrSizeAPIResponse) flickrSizeAPIResponses[i];
            if (listOfPhotoItems.size() < totalPhotos && response.getSizes() != null
                && response.getSizes().getAvailableSizeList() != null) {

                //get the first item of the array for the square size
                Size size = response.getSizes().getAvailableSizeList().get(0);
                if (size != null) {
                    String photoId = getPhotoIds(size.getSource());
                    PhotoItem photoItem = photoItemsMap.get(photoId);
                    if (photoItem != null) {
                        photoItem.setUrl(size.getSource());
                        listOfPhotoItems.add(photoItem);
                        PhotoItem duplicateItem =
                            new PhotoItem(photoItem.getId(), photoItem.getSecret(),
                                photoItem.getServer(), photoItem.getFarm(), photoItem.getUrl(),
                                photoItem.isOpen());
                        listOfPhotoItems.add(duplicateItem);
                    }
                }
            }
        }
        Collections.shuffle(listOfPhotoItems);
        photoPresenter = new PhotoPresenter(this, listOfPhotoItems);
        photoAdapter.setListOfPhotoItems(listOfPhotoItems);
        recyclerView.setAdapter(photoAdapter);
    }

    //a method to return a photo id from the returned values from size api since there is no direct map to sizes and a particular photo
    private String getPhotoIds(String url) {
        int starIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.indexOf("_");
        return url.substring(starIndex, endIndex).trim();
    }

    @Override public void onClick(
        View view, int position) {
        totalClicks++;
        if (totalClicks <= Constant.TOTAL_ALLOWED_CLICK) {
            int x = position / Constant.NO_OF_ROWS;
            int y = position % Constant.NO_OF_ROWS;
            if (firstClickedView == null) firstClickedView = view;
            clickedView = view;
            photoPresenter.onPhotoClicked(x, y);
        }
    }
}
