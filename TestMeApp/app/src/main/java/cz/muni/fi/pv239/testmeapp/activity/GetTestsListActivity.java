package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.adapter.TestLightAdapter;
import cz.muni.fi.pv239.testmeapp.api.GithubApi;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.fragment.TestDialogFragment;
import cz.muni.fi.pv239.testmeapp.model.TestLight;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Franta on 29.03.2018.
 */

public class GetTestsListActivity extends AppCompatActivity{
    private TestLightAdapter mAdapter;

    private Unbinder mUnbinder;
    private Realm mRealm;
    private GithubApi mGithubApi;
    private TestDialogFragment mNotFoundDialog;
    private FragmentManager mFragmentManager;

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
        mList.setLayoutManager(new LinearLayoutManager(this));
        mRealm = Realm.getDefaultInstance();

        mFragmentManager = this.getSupportFragmentManager();

        mNotFoundDialog = TestDialogFragment.newInstance(10);
        mNotFoundDialog.onCreate(mNotFoundDialog.getArguments());

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
                if (response.code() != 404) {
                    populateList(response.body());
                    if (mProgressBar != null){
                        mProgressBar.setVisibility(View.GONE);
                    }
                    if (mList != null) {
                        mList.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mProgressBar != null){
                        mProgressBar.setVisibility(View.GONE);
                    }

                    if (mFragmentManager != null) {
                        FragmentTransaction ft = mFragmentManager.beginTransaction();
                        Fragment notFoundDialog = mFragmentManager.findFragmentByTag("mNotFoundDialog");
                        if (notFoundDialog != null) {
                            ft.remove(notFoundDialog);
                        }

                        TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.WRONG_ACCESS_DATA);
                        mDialog.onCreate(mDialog.getArguments());
                        ft.add(mDialog, "mNotFoundDialog");
                        ft.commitAllowingStateLoss();
                    }


                }
            }

            @Override
            public void onFailure(Call<List<TestLight>> call, Throwable t) {
                t.printStackTrace();
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }

                if (mLoadingFailed != null){
                    mLoadingFailed.setVisibility(View.VISIBLE);
                }
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