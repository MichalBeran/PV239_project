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
import android.widget.Button;

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

    public void downloadTest(@NonNull final String testUrl){
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.test_downloading));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);

        final Dialog m404Dialog;
        final Dialog mDialog;
        final Dialog mSuccessDialog;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        m404Dialog = mBuilder.setTitle(R.string.test_download_failed_bad_response)
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(this);
        mDialog = mBuilder1.setTitle(R.string.test_download_failed)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadTest(testUrl);
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(this);
        mSuccessDialog = mBuilder2.setTitle(R.string.test_save_successful)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        final String path = Uri.parse(testUrl).getPath();
        final Call<Test> testCall = mTestApi.getService().getTest(path);

        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testCall.cancel();
                dialog.dismiss();
            }
        });
        mProgressDialog.show();

        testCall.enqueue(new Callback<Test>() {

            @Override
            public void onResponse(Call<Test> call, retrofit2.Response<Test> response) {
                if (response.code() == 404 || response.code() == 400){
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        m404Dialog.show();
                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                if (m404Dialog.isShowing()) {
                                    m404Dialog.dismiss();
                                }
                                t.cancel();
                            }
                        }, 2000);
                    }
                }else {
                    Test test = response.body();
                    if (test == null) {
                        return;
                    }
                    test.url = mTestApi.getUrlBase() + path;
                    Boolean state = saveResult(test);
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mSuccessDialog.show();
                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                if (mSuccessDialog.isShowing()) {
                                    mSuccessDialog.dismiss();
                                }
                                t.cancel();
                            }
                        }, 3000);
                    }
                }
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
                if(mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                    mDialog.show();
                }
            }
        });
    }

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
        mAdapter = new TestLightAdapter(new ArrayList<TestLight>(), this);
        mList.setAdapter(mAdapter);
        mTestApi = new TestApi();
        mList.setLayoutManager(new LinearLayoutManager(this));
        mRealm = Realm.getDefaultInstance();


        loadTests("MichalBeran", "PV239_project");
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

    private Boolean saveResult(final Test test) {
        Realm realm = null;
        Boolean state = false;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(test);
                }
            });
            state = true;
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
        return state;
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

    private void loadTests(@NonNull String username, @NonNull String repositoryName) {
        Call<List<TestLight>> listsCall = mGithubApi.getService().getTestsList(username, repositoryName);
        listsCall.enqueue(new Callback<List<TestLight>>() {

            @Override
            public void onResponse(Call<List<TestLight>> call, Response<List<TestLight>> response) {
                populateList(response.body());
            }

            @Override
            public void onFailure(Call<List<TestLight>> call, Throwable t) {
                t.printStackTrace();
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