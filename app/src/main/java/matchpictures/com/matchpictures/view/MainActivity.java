package matchpictures.com.matchpictures.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        tvTotalFlips = findViewById(R.id.tv_total_flip);
        tvFinishGame = findViewById(R.id.tv_done_msg);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(view -> photoPresenter.reset());
    }

    @Override protected void onResume() {
        super.onResume();
        preparePhotos();
    }

    @Override public void updateFlip(int totalFlips) {
        tvTotalFlips.setText(
            String.format(getString(R.string.no_of_flips), String.valueOf(totalFlips)));
    }

    @Override public void flipOver() {
        //ImageView ivCover = clickedView.findViewById(R.id.iv_photo_cover);
        //ivCover.setBackgroundColor(Color.TRANSPARENT);
        Log.d("MHH", "Flipover");
    }

    @Override public void flipBack() {
        //ImageView ivFirstPhotoCover = firstClickedView.findViewById(R.id.iv_photo_cover);
        //ivFirstPhotoCover.setBackgroundColor(Color.GRAY);
        //ImageView ivSecondPhotoCover = clickedView.findViewById(R.id.iv_photo_cover);
        //ivSecondPhotoCover.setBackgroundColor(Color.GRAY);
        firstClickedView = null;
        totalClicks = 0;
        Log.d("MHH", "Flipblack");
    }

    @Override public void finishGame() {
        tvFinishGame.setVisibility(View.VISIBLE);
    }

    @Override public void resetView() {
        Collections.shuffle(listOfPhotoItems);
        tvFinishGame.setVisibility(View.GONE);
        photoPresenter.setPhotoItems(listOfPhotoItems);
        photoAdapter.setListOfPhotoItemsAfterReset(listOfPhotoItems);
    }

    private void preparePhotos() {
        Call<FlickrAPIResponse> response = APIService.provideGetRecentPhotosService()
            .getRecentPhotos("5423dbab63f23a62ca4a986e7cbb35e2", "flickr.photos.getRecent", "json",
                "8");
        response.enqueue(new Callback<FlickrAPIResponse>() {
            @Override public void onResponse(
                Call<FlickrAPIResponse> call, Response<FlickrAPIResponse> response) {
                if (response.body() != null && response.body().getPhotos() != null) {
                    getAvailableSizes(response.body().getPhotos().getPhoto());
                }
            }

            @Override public void onFailure(Call<FlickrAPIResponse> call, Throwable t) {
            }
        });
    }

    @SuppressLint("CheckResult") private void getAvailableSizes(List<PhotoItem> photos) {
        List<Observable<?>> requests = new ArrayList<>();
        GetAvailableSizesService getAvailableSizesService = APIService.provideAvailableSize();
        for (PhotoItem photoItem : photos) {
            photoItemsMap.put(photoItem.getId(), photoItem);
            requests.add(
                getAvailableSizesService.getAvailableSizes("5423dbab63f23a62ca4a986e7cbb35e2",
                    "flickr.photos.getSizes", "json", photoItem.getId()));
        }
        // Zip all requests with the Function, which will receive the results.
        Observable.zip(requests, objects -> objects)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(objects -> preparePhotoItemsWithUrls(objects), e -> {
                //Do something on error completion of requests
            });
    }

    private void preparePhotoItemsWithUrls(Object[] flickrSizeAPIResponses) {
        for (int i = 0; i < flickrSizeAPIResponses.length; i++) {
            FlickrSizeAPIResponse response = (FlickrSizeAPIResponse) flickrSizeAPIResponses[i];
            if (listOfPhotoItems.size() < 16 && response.getSizes() != null
                && response.getSizes().getAvailableSizeList() != null) {

                //get the first item of the array for the square size
                Size size = response.getSizes().getAvailableSizeList().get(0);
                if (size != null) {
                    String photoId = getPhotoIds(size.getSource());
                    PhotoItem photoItem = photoItemsMap.get(photoId);
                    if (photoItem != null) {
                        photoItem.setUrl(size.getSource());
                        listOfPhotoItems.add(photoItem);
                        listOfPhotoItems.add(photoItem);
                    }
                }
            }
        }
        Collections.shuffle(listOfPhotoItems);
        photoPresenter = new PhotoPresenter(this, listOfPhotoItems);
        photoAdapter.setListOfPhotoItems(listOfPhotoItems);
        recyclerView.setAdapter(photoAdapter);
    }

    private String getPhotoIds(String url) {
        int starIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.indexOf("_");
        return url.substring(starIndex, endIndex).trim();
    }

    @Override public void onClick(
        View view, int position) {
        totalClicks++;
        if (totalClicks <= 2) {
            int x = position / 4;
            int y = position % 4;
            if (firstClickedView == null) firstClickedView = view;
            clickedView = view;
            photoPresenter.onPhotoClicked(x, y);
        }
    }
}
