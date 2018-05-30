package cz.muni.fi.pv239.testmeapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.adapter.TestLightAdapter;
import cz.muni.fi.pv239.testmeapp.adapter.TestsAdapter;
import cz.muni.fi.pv239.testmeapp.api.GithubApi;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import cz.muni.fi.pv239.testmeapp.model.TestLight;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Franta on 29.03.2018.
 */

public class GetTestsListActivity extends AppCompatActivity{
    private TestLightAdapter mAdapter;

    private TestApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private GithubApi mGithubApi;

    @BindView(android.R.id.list)
    RecyclerView mList;

    @BindView(R.id.loadingBar)
    RelativeLayout mProgressBar;

    @BindView(R.id.loadingFailed)
    TextView mLoadingFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_download_list_tests);
        mUnbinder = ButterKnife.bind(this);
        mGithubApi = new GithubApi();
        mAdapter = new TestLightAdapter(new ArrayList<TestLight>());
        mList.setAdapter(mAdapter);
        mTestApi = new TestApi();
        mList.setLayoutManager(new LinearLayoutManager(this));
        mRealm = Realm.getDefaultInstance();


        loadTests(TestMeApp.getGitUser(this), TestMeApp.getGitRepo(this), TestMeApp.getGitFolder(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.list_tests_activity_head);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mRealm.close();
    }
    
    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context,GetTestsListActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadTests(@NonNull String username, @NonNull String repositoryName, @NonNull String repositoryFolder) {
        Call<List<TestLight>> listsCall = mGithubApi.getService().getTestsList(username, repositoryName, repositoryFolder);
        listsCall.enqueue(new Callback<List<TestLight>>() {

            @Override
            public void onResponse(Call<List<TestLight>> call, Response<List<TestLight>> response) {
                populateList(response.body());
                mProgressBar.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<TestLight>> call, Throwable t) {
                t.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
                mLoadingFailed.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateList(List<TestLight> tests) {
        if (tests  == null) {
            return;
        }

        mAdapter.refreshTests(tests);
    }
}