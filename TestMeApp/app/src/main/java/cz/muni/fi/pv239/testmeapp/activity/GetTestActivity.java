package cz.muni.fi.pv239.testmeapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
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

    private void loadTest(@NonNull final String testUrl) {
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
                        loadTest(testUrl);
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
//                Snackbar.make(findViewById(R.id.getTestLayout), R.string.test_download_failed, Snackbar.LENGTH_LONG)
//                        .setAction(R.string.retry, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // Respond to the click, such as by undoing the modification that caused
//                                // this message to be displayed
//                                loadTest(testname);
//                            }
//                        }).show();
                if(mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                    mDialog.show();
                }
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
//            Snackbar.make(findViewById(R.id.getTestLayout), R.string.test_save_successful, Snackbar.LENGTH_LONG).show();
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
}
