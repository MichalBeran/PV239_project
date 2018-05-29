package cz.muni.fi.pv239.testmeapp.api;

import java.util.List;

import cz.muni.fi.pv239.testmeapp.model.TestLight;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Franta on 29.03.2018.
 */

public interface GithubService {

    final static String GITHUB_USER = "https://api.github.com";
    final static String GITHUB_REPO = "https://api.github.com";
    // https://developer.github.com/v3/activity/watching/


    //curl -i https://api.github.com/repos/MichalBeran/PV239_project/contents/Tests
    @GET("repos/{username}/{reponame}/contents/{folder}")
    Call<List<TestLight>> getTestsList(@Path("username") String username, @Path("reponame") String reponame, @Path("folder") String folder);

}