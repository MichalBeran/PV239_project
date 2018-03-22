package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.testApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
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
}
