package matchpictures.com.matchpictures.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import java.util.List;
import matchpictures.com.matchpictures.PhotoAdapter;
import matchpictures.com.matchpictures.R;
import matchpictures.com.matchpictures.apiService.APIService;
import matchpictures.com.matchpictures.model.FlickrAPIResponse;
import matchpictures.com.matchpictures.model.PhotoItem;
import matchpictures.com.matchpictures.presenter.PhotoPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IPhotoView {
    private PhotoPresenter photoPresenter;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoPresenter = new PhotoPresenter(this);
        photoAdapter = new PhotoAdapter(this);
        recyclerView = findViewById(R.id.rc_photos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override protected void onResume() {
        super.onResume();
        preparePhotos();
    }

    @Override public void updateFlip(int flip) {

    }

    @Override public void flipOver() {

    }

    private void preparePhotos() {
        Call<FlickrAPIResponse> response =
            APIService.provideGetRecentPhotosService().getRecentPhotos(
                "5423dbab63f23a62ca4a986e7cbb35e2", "flickr.photos.getRecent", "json", "8");
        response.enqueue(new Callback<FlickrAPIResponse>() {
            @Override public void onResponse(
                Call<FlickrAPIResponse> call, Response<FlickrAPIResponse> response) {
                if (response.body() != null) {
                    displayPhotos(response.body().getPhoto());
                }
            }

            @Override public void onFailure(Call<FlickrAPIResponse> call, Throwable t) {
                Log.d("MHH", t.getMessage());
            }
        });
    }

    private void displayPhotos(List<PhotoItem> listOfPhotos) {
        for (PhotoItem photoItem : listOfPhotos) {
            setPhotoURLs(photoItem);
        }
        photoAdapter.setListOfPhotoItems(listOfPhotos);
        recyclerView.setAdapter(photoAdapter);
    }

    private PhotoItem setPhotoURLs(PhotoItem photoItem) {
        String url =
            "https://farm" + photoItem.getFarm() + ".staticflickr.com/" + photoItem.getServer()
                + "/" + photoItem.getId() + "_" + photoItem.getSecret();
        photoItem.setUrl(url);
        return photoItem;
    }
}
