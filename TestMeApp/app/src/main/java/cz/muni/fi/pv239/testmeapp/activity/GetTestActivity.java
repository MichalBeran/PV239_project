package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.testApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import io.realm.Realm;

/**
 * Created by Michal on 21.03.2018.
 */

public class GetTestActivity extends AppCompatActivity{

    private testApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_test);
        mTestApi = new testApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        TextView text = findViewById(R.id.jsonResult);
        RealmResults<Test> tests = mRealm.where(Test.class).findAll();
        if (!tests.isEmpty()) {
            Test test = tests.first();
            text.setText(test.url + "\n" + test.name + " " + test.testDuration + " " + test.questions.first().text);
        }
    }

    @OnClick(R.id.urlButton)
    protected void load(){
        EditText text = findViewById(R.id.urlText);
        loadTest(text.getText().toString());
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

                TextView text = findViewById(R.id.jsonResult);
                text.setText(test.name + " " + test.testDuration + " " + test.questions.first().text);
                test.url = mTestApi.getUrlBase() + testname;
                saveResult(test);

            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
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
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
    }
}
