package matchpictures.com.matchpictures.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
    private PhotoAdapter photoAdapter;
    private List<PhotoItem> listOfPhotoItems = new ArrayList<>();
    private HashMap<String, PhotoItem> photoItemsMap = new HashMap<>();
    private HashMap<Integer, PhotoItem> flippedPhotos = new HashMap<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoPresenter = new PhotoPresenter(this);
        photoAdapter = new PhotoAdapter(this, this);
        recyclerView = findViewById(R.id.rc_photos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override protected void onResume() {
        super.onResume();
        preparePhotos();
    }

    @Override public void updateFlip(int flip) {
        Log.d("MHH", "update flip");
    }

    @Override public void flipOver() {
        Log.d("MHH", "Flip over");
    }

    @Override public void flipBack() {
        Log.d("MHH", "Flip back");
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
                Log.d("MHH", t.getMessage());
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
        photoAdapter.setListOfPhotoItems(listOfPhotoItems);
        recyclerView.setAdapter(photoAdapter);
    }

    private String getPhotoIds(String url) {
        int starIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.indexOf("_");
        return url.substring(starIndex, endIndex).trim();
    }

    @Override public void onClick(
        View view, PhotoItem photoItem, int position) {
        if (!flippedPhotos.containsKey(position)) {
            flippedPhotos.put(position, photoItem);
            photoPresenter.flip(photoItem, flippedPhotos.size() == 2 ? true : false);
            if (flippedPhotos.size() == 2) flippedPhotos.clear();
        }
    }
}
