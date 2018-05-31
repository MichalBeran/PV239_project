package cz.muni.fi.pv239.testmeapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Michal on 24.03.2018.
 */

public class ScanQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private TestApi mTestApi;
    private Realm mRealm;
    private ZXingScannerView mScannerView;

    private static final int REQUEST_GET_ACCOUNT = 112;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_get_test);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mTestApi = new TestApi();
        mRealm = Realm.getDefaultInstance();
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.scan_qr_activity_head);
        startCamera();
    }

    @Override
    public void handleResult(Result rawResult){
        mScannerView.removeAllViews();  // here remove all the views, it will make an Activity having no View
        mScannerView.stopCamera();  // then stop the camera
        loadTest(rawResult.getText());
        finish();
    }

    private void showCameraPermissionRationale(final Context context) {
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permision_needed);
        alertBuilder.setMessage(R.string.permision_camera_rationale);
        alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // this way you can get to the screen to set the permissions manually
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        android.support.v7.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void loadTest(@NonNull final String testUrl) {
        final String path = Uri.parse(testUrl).getPath();
        Call<Test> testCall = mTestApi.getService().getTest(path);
        testCall.enqueue(new Callback<Test>() {

            @Override
            public void onResponse(Call<Test> call, retrofit2.Response<Test> response) {
                if (response.code() == 404 || response.code() == 400){
                    Toast.makeText(ScanQRCodeActivity.this, R.string.test_download_failed_bad_response, Toast.LENGTH_SHORT).show();
                }else {
                    Test test = response.body();
                    if (test == null) {
                        return;
                    }
                    test.url = mTestApi.getUrlBase() + path;
                    test.favourite = isFavouriteTest(test.url);
                    Boolean state = saveResult(test);
                }
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ScanQRCodeActivity.this, R.string.test_download_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean saveResult(final Test test) {
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
            Toast.makeText(ScanQRCodeActivity.this, R.string.test_save_successful, Toast.LENGTH_SHORT).show();
            state = true;
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
        return state;
    }

    private void startCamera(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Lower than Marshmallow -> permissions were granted during the install process
            mScannerView.startCamera();
        } else {
            // Let's check whethere we already have the permission
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted -> save the data
                mScannerView.startCamera();
            } else {
                // Android helper method to tell us if it's useful to show a hint
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    // Show some alert explaining why it is important to grant the permission
                    showCameraPermissionRationale(this);
                } else {
                    // Just straight to the point
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
            }
        }
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

    private boolean isFavouriteTest(String url) {
        Test test = mRealm.where(Test.class)
                .equalTo("url", url)
                .findFirst();
        if (test == null) {
            return false;
        }
        return test.favourite;
    }
}
