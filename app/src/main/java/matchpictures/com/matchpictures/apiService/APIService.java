package matchpictures.com.matchpictures.apiService;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import matchpictures.com.matchpictures.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static Interceptor flickrRequestInterceptor = chain -> {
        Request request = chain.request();
        request = request.newBuilder().build();
        return chain.proceed(request);
    };
    private static HttpUrl endPoint = HttpUrl.parse("https://api.flickr.com/");

    public static Retrofit provideAdapter() {
        okhttp3.OkHttpClient client = new OkHttpClient();
        final OkHttpClient.Builder builder = client.newBuilder().addInterceptor(
            flickrRequestInterceptor);
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder().client(builder.build())
            .baseUrl(endPoint)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());
        return retrofitBuilder.build();
    }

    public static GetRecentPhotosService provideGetRecentPhotosService() {
        return provideAdapter().create(GetRecentPhotosService.class);
    }

    public static GetAvailableSizesService provideAvailableSize() {
        return provideAdapter().create(GetAvailableSizesService.class);
    }
}
