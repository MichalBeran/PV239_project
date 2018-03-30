package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;

public class MainActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    private String actualLanguage;

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualLanguage = TestMeApp.getLang(this);
        TestMeApp.changeLang(this, TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getBaseContext(), TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getApplicationContext(), TestMeApp.getLang(TestMeApp.appContext));
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
    }

    @OnClick(R.id.floatingDownload)
    protected void floatingDownloadTests(){
        downloadTests();
    }

    @OnClick(R.id.downloadTest)
    protected void downloadTests(){
        Intent intent = GetTestActivity.newIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.showTests)
    protected void showTests(){
        Intent intent = ListTestsActivity.newIntent(this);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.main_activity_head);
        if (!(actualLanguage.equals(TestMeApp.getLang(TestMeApp.appContext)))){
            actualLanguage = TestMeApp.getLang(TestMeApp.appContext);
            recreate();
        }
    }
}
