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
import android.widget.Toast;

import com.google.zxing.Result;

import java.net.URI;
import java.net.URISyntaxException;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.testApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Michal on 24.03.2018.
 */

public class ScanQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private testApi mTestApi;
    private Realm mRealm;
    private ZXingScannerView mScannerView;

    private static final int REQUEST_GET_ACCOUNT = 112;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_get_test);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mTestApi = new testApi();
        mRealm = Realm.getDefaultInstance();
        startCamera();
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
        startCamera();
    }

    @Override
    public void handleResult(Result rawResult){
        Toast.makeText(ScanQRCodeActivity.this, "SCAN SUCCESFULL", Toast.LENGTH_SHORT).show();
        URI uri = null;
        try {
            uri = new URI(rawResult.getText());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String path = uri.getPath();
        String end = path.substring(path.lastIndexOf('/') + 1);

        mScannerView.removeAllViews();  // here remove all the views, it will make an Activity having no View
        mScannerView.stopCamera();  // then stop the camera
        loadTest(end);
        finish();
    }

    private void showCameraPermissionRationale(final Context context) {
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Granting the permission needed");
        alertBuilder.setMessage("Hi there, the app needs to access to camera to read QR code.");
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

    private void loadTest(@NonNull final String testname) {
        Call<Test> testCall = mTestApi.getService().getTest(testname);
        testCall.enqueue(new Callback<Test>() {

            @Override
            public void onResponse(Call<Test> call, retrofit2.Response<Test> response) {
                Test test = response.body();
                if (test == null) {
                    return;
                }
                test.url = mTestApi.getUrlBase() + testname;
                saveResult(test);
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ScanQRCodeActivity.this, "DOWNLOAD FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveResult(final Test test) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(test);
                }
            });
            Toast.makeText(ScanQRCodeActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
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
}
