package matchpictures.com.matchpictures;

import matchpictures.com.matchpictures.model.FlickrAPIResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRecentPhotosService {
    @GET("/services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1")
    Call<FlickrAPIResponse> getRecentPhotos(@Query("api_key") String apiKey);
}
