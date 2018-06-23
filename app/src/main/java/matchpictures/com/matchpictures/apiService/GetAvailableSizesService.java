package matchpictures.com.matchpictures.apiService;

import matchpictures.com.matchpictures.model.FlickrSizeAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetAvailableSizesService {
    @GET("/services/rest/?nojsoncallback=1") Call<FlickrSizeAPIResponse> getAvailableSizes(
        @Query("api_key") String apiKey, @Query("method") String method,
        @Query("format") String format, @Query("photo_id") String photoId);
}
