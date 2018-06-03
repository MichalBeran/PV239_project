package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.fragment.TestDialogFragment;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Michal on 21.03.2018.
 */

public class GetTestActivity extends AppCompatActivity{

    private TestApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private FragmentManager mFragmentManager;
    private Call<Test> testCall;

    @BindView(R.id.urlText)
    EditText mUrlText;

    @BindView(R.id.floatingButtonDownload)
    android.support.design.widget.FloatingActionButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_test);
        mTestApi = new TestApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();

        mUrlText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitButton.performClick();
                    return true;
                }
                return false;
            }
        });

//        Redundantni this
        mFragmentManager = this.getSupportFragmentManager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.get_test_activity_head);
    }

    @OnClick(R.id.floatingButtonDownload)
    protected void load(){
        loadTest(mUrlText.getText().toString());
    }

    public void loadTest(@NonNull final String testUrl) {
        final String path = Uri.parse(testUrl).getPath();
        testCall = mTestApi.getService().getTest(path);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment prev = mFragmentManager.findFragmentByTag("mProgressDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        TestDialogFragment mProgDialog = TestDialogFragment.newInstance(TestDialogFragment.DOWNLOAD_PROGRESS_DIALOG);
        mProgDialog.onCreate(mProgDialog.getArguments());
        ft.add(mProgDialog, "mProgressDialog");
        ft.commitAllowingStateLoss();

        testCall.enqueue(new Callback<Test>() {
            @Override
            public void onResponse(Call<Test> call, retrofit2.Response<Test> response) {
                if (response.code() == 404 || response.code() == 400 || response.code() == 406){
                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    Fragment progFragment = mFragmentManager.findFragmentByTag("mProgressDialog");
                    ft.remove(progFragment);
                    ft.commitAllowingStateLoss();

                    ft = mFragmentManager.beginTransaction();
                    Fragment prev = mFragmentManager.findFragmentByTag("m404Dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.FAILURE_404_DIALOG);
                    mDialog.onCreate(mDialog.getArguments());
                    ft.add(mDialog, "m404Dialog");
                    ft.commitAllowingStateLoss();

                }else {
                    Test test = response.body();
                    if (test == null) {
                        return;
                    }
                    test.url = mTestApi.getUrlBase() + path;
                    test.favourite = isFavouriteTest(test.url);
                    Boolean state = saveResult(test);

                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    Fragment progFragment = mFragmentManager.findFragmentByTag("mProgressDialog");
                    ft.remove(progFragment);
                    ft.commitAllowingStateLoss();

                    ft = mFragmentManager.beginTransaction();
                    Fragment prev = mFragmentManager.findFragmentByTag("mSuccessDialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.SUCCESS_DIALOG);
                    mDialog.onCreate(mDialog.getArguments());
                    ft.add(mDialog, "mSuccessDialog");
                    ft.commitAllowingStateLoss();

                    }
                }


            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                TestDialogFragment progFragment = (TestDialogFragment) mFragmentManager.findFragmentByTag("mProgressDialog");
                ft.remove(progFragment);
                ft.commitAllowingStateLoss();

                ft = mFragmentManager.beginTransaction();
                TestDialogFragment retryFragment = TestDialogFragment.newInstance(TestDialogFragment.RETRY_DIALOG);
                ft.add(retryFragment, "mRetryDialog");
                ft.commitAllowingStateLoss();

            }
        });
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, GetTestActivity.class);
        return intent;
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

    public void CancelTestCall(){
        if(testCall != null){
            testCall.cancel();
        }
    }

    private boolean isFavouriteTest(String url) {
        Test test = mRealm.where(Test.class)
                .equalTo("url", url)
                .findFirst();
        if (test == null) {
            return false;
        }
        return test.favourite;
    }

    public String getUrlText() {
        return mUrlText.getText().toString();
    }
}
