package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.testApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Michal on 22.03.2018.
 */

public class ListTestsActivity extends AppCompatActivity {
    private testApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tests);
        mTestApi = new testApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        TextView text = findViewById(R.id.showFirstTest);
        RealmResults<Test> tests = mRealm.where(Test.class).findAll();
        if (!tests.isEmpty()) {
            Test test = tests.first();
            text.setText("URL:" + test.url + "\n"
                    + "NAME:" + test.name + "\n DURATION:" + test.testDuration + "\n FIRST QUESTION:" + test.questions.first().text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mRealm.close();
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context,ListTestsActivity.class);
        return intent;
    }
}
