package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;

/**
 * Created by Michal on 22.03.2018.
 */

public class ShowTestActivity extends AppCompatActivity {
    private TestApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private Test mTest;

    @BindView(R.id.testURL)
    TextView testUrl;

    @BindView(R.id.removeTest)
    Button removeButton;

    @BindView(R.id.shareQr)
    Button shareQrButon;

    @BindView(R.id.runDrill)
    Button runDrillButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        mTestApi = new TestApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        String url = getIntent().getStringExtra("url");
        mTest = mRealm.where(Test.class).equalTo("url", url).findFirst();
        testUrl.setText("URL: " + mTest.url + "\n" +
            "First Question" + mTest.questions.first().text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.show_test_activity_head);
    }

    @Override
    public void onBackPressed() {
        Intent intent = ListTestsActivity.newIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.removeTest)
    public void removeTest(){
        mRealm.beginTransaction();
        mTest.deleteFromRealm();
        mRealm.commitTransaction();
        finish();
    }

    @OnClick(R.id.shareQr)
    public void shareQrCode(){
        Intent intent = CreateQRCodeActivity.newIntent(this);
        intent.putExtra("qr", mTest.url);
        startActivity(intent);
    }

    @OnClick(R.id.runDrill)
    public void runTestDrill(){
        //TODO: add NumberPickerDialog
        Intent intent = RunDrillTestActivity.newIntent(this, mTest.questions.size());
        String[] urlSplit = mTest.url.split("/");
        intent.putExtra("testFileName", urlSplit[urlSplit.length - 1]);
        intent.putExtra("testName", mTest.name);
        intent.putExtra("url", mTest.url);
        intent.putExtra("questionsLeft", 0);
        startActivity(intent);
    }

    @OnClick(R.id.runTest)
    public void runTest() {
        //TODO: implement testRunActivity
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Run test")
                .setMessage("Not implemented.")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, ShowTestActivity.class);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mRealm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mTest.url);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
                return true;
            case R.id.share_qr:
                shareQrCode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
