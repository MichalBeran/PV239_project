package cz.muni.fi.pv239.testmeapp.api;

import cz.muni.fi.pv239.testmeapp.model.Test;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Michal on 21.03.2018.
 */

public interface TestService {
    // https://developer.github.com/v3/users/
    @Headers({
            "Accept: application/vnd.github.v3.raw"
    })
    @GET("{testname}")
    Call<Test> getTest(@Path("testname") String testname);
}
