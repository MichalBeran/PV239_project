package cz.muni.fi.pv239.testmeapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_test);
        mTestApi = new TestApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.get_test_activity_head);
    }

    @OnClick(R.id.urlButton)
    protected void load(){
        EditText text = findViewById(R.id.urlText);
        loadTest(text.getText().toString());
    }

    private void loadTest(@NonNull final String testname) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.test_downloading));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);

        final Dialog mDialog;
        final Dialog mSuccessDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.setTitle(R.string.test_download_failed)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadTest(testname);
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mSuccessDialog = mBuilder.setTitle(R.string.test_save_successful)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        final Call<Test> testCall = mTestApi.getService().getTest(testname);

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
                Test test = response.body();
                if (test == null) {
                    return;
                }
                test.url = mTestApi.getUrlBase() + testname;
                saveResult(test);
                if(mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                    mSuccessDialog.show();
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            if(mSuccessDialog.isShowing()) {
                                mSuccessDialog.dismiss();
                            }
                            t.cancel();
                        }
                    }, 3000);
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

    @OnClick(R.id.scanButton)
    public void scanQrCode(){
        Intent intent = ScanQRCodeActivity.newIntent(this);
        startActivity(intent);
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
