package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;

/**
 * Created by Michal on 22.03.2018.
 */

public class ShowTestActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private Realm mRealm;
    private Test mTest;

    @BindView(R.id.testURL)
    TextView testUrl;

    @BindView(R.id.removeTest)
    Button removeButton;

    @BindView(R.id.testName)
    TextView testName;

    @BindView(R.id.runDrill)
    Button runDrillButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        String url = getIntent().getStringExtra("url");
        mTest = mRealm.where(Test.class).equalTo("url", url).findFirst();
        testUrl.setText("URL: " + mTest.url + "\n" +
            "First Question" + mTest.questions.first().text);
        testName.setText(mTest.name);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = getTestResults();
        series.setColor(Color.parseColor("#FFBB33"));
        series.setThickness(10);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(100, 255, 187, 51));
        graph.addSeries(series);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.show_test_activity_head);
    }

    @OnClick(R.id.removeTest)
    public void removeTest(){
        mRealm.beginTransaction();
        mTest.deleteFromRealm();
        mRealm.commitTransaction();
        finish();
    }

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

    private LineGraphSeries<DataPoint> getTestResults(){
        return new LineGraphSeries<>(new DataPoint[] {
                //foreach result generate DataPoint x:iterator y:testResult

                //example data
                new DataPoint(0, 50),
                new DataPoint(1, 56),
                new DataPoint(2, 30),
                new DataPoint(3, 80),
                new DataPoint(4, 59)
        });
    }
}
