package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.api.testApi;
import cz.muni.fi.pv239.testmeapp.fragment.SettingsFragment;
import cz.muni.fi.pv239.testmeapp.model.Test;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    private String actualLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualLanguage = TestMeApp.getLang(this);
        TestMeApp.changeLang(this, TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getBaseContext(), TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getApplicationContext(), TestMeApp.getLang(TestMeApp.appContext));
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        setTitle("Menu");
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
        if (!(actualLanguage.equals(TestMeApp.getLang(TestMeApp.appContext)))){
            actualLanguage = TestMeApp.getLang(TestMeApp.appContext);
            recreate();
        }
    }
}
