package cz.muni.fi.pv239.testmeapp.api;

import cz.muni.fi.pv239.testmeapp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Michal on 21.03.2018.
 */

public class testApi {
    private final static String GITHUB_ENDPOINT = "https://raw.githubusercontent.com/MichalBeran/PV239_project/master/Tests/";
    private final testService mService;

    public testApi() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        final OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(testService.class);
    }

    public testService getService() {
        return mService;
    }
    public String getUrlBase(){
        return GITHUB_ENDPOINT;
    }
}
