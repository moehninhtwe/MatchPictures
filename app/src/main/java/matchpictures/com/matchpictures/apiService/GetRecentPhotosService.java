package matchpictures.com.matchpictures.apiService;

import matchpictures.com.matchpictures.model.FlickrAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRecentPhotosService {
    @GET("/services/rest/?nojsoncallback=1")
    Call<FlickrAPIResponse> getRecentPhotos(
        @Query("api_key") String apiKey, @Query("method") String method,
        @Query("format") String format, @Query("per_page") String perPage);
}
