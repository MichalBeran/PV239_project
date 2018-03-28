package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.adapter.TestsAdapter;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Michal on 22.03.2018.
 */

public class ListTestsActivity extends AppCompatActivity {
    private TestApi mTestApi;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private TestsAdapter mAdapter;

    @BindView(android.R.id.list)
    RecyclerView mList;

    @BindView(R.id.count)
    TextView mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tests);
        mTestApi = new TestApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        RealmResults<Test> tests = mRealm.where(Test.class).findAll();

        mAdapter = new TestsAdapter(this, tests);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setHasFixedSize(true);

        mCount.setText( "Number of tests: (" + Integer.toString(tests.size()) + ')');

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
