package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

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
        Intent intent = RunDrillTestActivity.newIntent(this);
        String[] urlSplit = mTest.url.split("/");
        intent.putExtra("testFileName", urlSplit[urlSplit.length - 1]);
        intent.putExtra("testName", mTest.name);
        startActivity(intent);
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
}
