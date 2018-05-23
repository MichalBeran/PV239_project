package cz.muni.fi.pv239.testmeapp.api;

/**
 * Created by Franta on 29.03.2018.
 */

import cz.muni.fi.pv239.testmeapp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import cz.muni.fi.pv239.testmeapp.BuildConfig;

public class GithubApi {

    private final static String GITHUB_API_ENDPOINT = "https://api.github.com";
    private final GithubService mService;

    public GithubApi() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        final OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_API_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(GithubService.class);
    }

    public GithubService getService() {
        return mService;
    }
}
