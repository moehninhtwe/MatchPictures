package matchpictures.com.matchpictures;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import matchpictures.com.matchpictures.model.FlickrAPIResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Call<FlickrAPIResponse> response = APIService.provideGetRecentPhotosService()
            .getRecentPhotos("5423dbab63f23a62ca4a986e7cbb35e2");
        response.enqueue(new Callback<FlickrAPIResponse>() {
            @Override public void onResponse(Call<FlickrAPIResponse> call,
                Response<FlickrAPIResponse> response) {
                Log.d("MHH", response.body().toString());
            }

            @Override public void onFailure(Call<FlickrAPIResponse> call, Throwable t) {
                Log.d("MHH", t.getMessage());
            }
        });
    }
}
