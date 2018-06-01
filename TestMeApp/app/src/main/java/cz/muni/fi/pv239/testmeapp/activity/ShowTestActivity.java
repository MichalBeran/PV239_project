package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.adapter.HistoryAdapter;
import cz.muni.fi.pv239.testmeapp.fragment.TestDialogFragment;
import cz.muni.fi.pv239.testmeapp.model.Test;
import cz.muni.fi.pv239.testmeapp.model.TestHistory;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Michal on 22.03.2018.
 */

public class ShowTestActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private Realm mRealm;
    private Test mTest;
    private Animation rotate_backward_90, rotate_forward_90, menu_open, menu_close;
    private boolean isMenuOpen = false;
    private HistoryAdapter mAdapter;

    @BindView(R.id.testNumQuestions)
    TextView testNumQuestions;

    @BindView(R.id.testDuration)
    TextView testDuration;

    @BindView(R.id.testMinPoints)
    TextView testMinPoints;

    @BindView(R.id.removeTest)
    Button removeButton;

    @BindView(R.id.testName)
    TextView testName;

    @BindView(R.id.floatingRunTest)
    FloatingActionButton floatingRunTest;

    @BindView(R.id.addToFavourites)
    Button addToFavoutiteButton;

    @BindView(R.id.runDrillButton)
    Button runDrillButton;

    @BindView(R.id.runTestButton)
    Button runTestButton;

    @BindView(R.id.historyRecyclerView)
    RecyclerView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        String url = getIntent().getStringExtra("url");
        mTest = mRealm.where(Test.class).equalTo("url", url).findFirst();
        testNumQuestions.setText(getString(R.string.text_test_count) + ": " + mTest.testCount);
        testDuration.setText(getString(R.string.text_test_duration) + ": " + mTest.testDuration);
        testMinPoints.setText(getString(R.string.text_test_min_points) + ": " + mTest.testMinPoint);
        testName.setText(mTest.name);

        rotate_backward_90 = AnimationUtils.loadAnimation(this, R.anim.rotate_backward_90);
        rotate_forward_90 = AnimationUtils.loadAnimation(this, R.anim.rotate_forward_90);
        menu_open = AnimationUtils.loadAnimation(this, R.anim.menu_open);
        menu_close = AnimationUtils.loadAnimation(this, R.anim.menu_close);
        //        API 19 drawableLeft substitution
        runDrillButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_autorenew_white_24dp), null, null, null);
        runDrillButton.setCompoundDrawablePadding(10);
        runTestButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_play_circle_filled_white_24dp), null, null, null);
        runTestButton.setCompoundDrawablePadding(10);
        removeButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_delete_white_24dp), null, null, null);
        removeButton.setCompoundDrawablePadding(10);
        addToFavoutiteButton.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_star_white_24dp), null, null, null);
        addToFavoutiteButton.setCompoundDrawablePadding(10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.show_test_activity_head);
        getTestResultsGraph();
        if (mTest.favourite){
            Drawable background = addToFavoutiteButton.getBackground();
            background.mutate();
            background.setColorFilter(ContextCompat.getColor(this, R.color.colorFavouriteYellow), PorterDuff.Mode.MULTIPLY);
            addToFavoutiteButton.setBackground(background);
        }else{
            Drawable background = addToFavoutiteButton.getBackground();
            background.mutate();
            background.setColorFilter(ContextCompat.getColor(this, R.color.colorFavouriteGray), PorterDuff.Mode.MULTIPLY);
            addToFavoutiteButton.setBackground(background);
        }

        RealmResults<TestHistory> history = mRealm.where(TestHistory.class).equalTo("testURL", mTest.url).findAllSorted("date", Sort.DESCENDING);
        mAdapter = new HistoryAdapter(this, history);
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setHasFixedSize(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isMenuOpen){
            animateMenu();
        }
    }

    @OnClick(R.id.removeTest)
    public void removeTest(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("mRemoveTestDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.REMOVE_TEST_DIALOG);
        mDialog.onCreate(mDialog.getArguments());
        ft.add(mDialog, "mRemoveTestDialog");
        ft.commitAllowingStateLoss();

    }

    public void removeThisTest(){
        final RealmResults<TestHistory> history = mRealm.where(TestHistory.class).equalTo("testURL", mTest.url).findAll();
        mRealm.beginTransaction();
        for(int i = history.size()-1; i >= 0; i--){
            history.get(i).deleteFromRealm();
        }
        mTest.deleteFromRealm();
        mRealm.commitTransaction();
    }

    @OnClick(R.id.addToFavourites)
    public void addToFavourite(){
        Test test = mRealm.where(Test.class).equalTo("url", mTest.url).findFirst();
        mRealm.beginTransaction();
        if(test.favourite){
            test.favourite = false;
            Drawable background = addToFavoutiteButton.getBackground();
            background.mutate();
            background.setColorFilter(ContextCompat.getColor(this, R.color.colorFavouriteGray), PorterDuff.Mode.MULTIPLY);
            addToFavoutiteButton.setBackground(background);
        }else{
            test.favourite = true;
            Drawable background = addToFavoutiteButton.getBackground();
            background.mutate();
            background.setColorFilter(ContextCompat.getColor(this, R.color.colorFavouriteYellow), PorterDuff.Mode.MULTIPLY);
            addToFavoutiteButton.setBackground(background);
        }
        mRealm.insertOrUpdate(test);
        mRealm.commitTransaction();

    }

    @OnClick(R.id.runDrillButton)
    public void runTestDrill(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("mDrillNumberPickerDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.DRILL_NUMBER_PICKER);
        mDialog.onCreate(mDialog.getArguments());
        ft.add(mDialog, "mDrillNumberPickerDialog");
        ft.commitAllowingStateLoss();
    }

    @OnClick(R.id.runTestButton)
    public void runTest() {
        Intent intent = RunTestActivity.newIntent(this, mTest.testCount);
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

    public void startDrillTest(int numberOfQuestions) {
        Intent intent = RunDrillTestActivity.newIntent(this, numberOfQuestions);
        intent.putExtra("testName", mTest.name);
        startActivity(intent);
    }

    private void shareQrCode(){
        Intent intent = CreateQRCodeActivity.newIntent(this);
        intent.putExtra("qr", mTest.url);
        startActivity(intent);
    }

    public NumberPicker setUpNumberPicker(View dialogView) {
        NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.number_picker);

        numberPicker.setMaxValue(setMaxNumberPickerValue());
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i("value", "new value: " + newVal);
            }
        });
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(getCorrectNumberOfQuestions(value));
            }
        });
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        return numberPicker;
    }

    public int getCorrectNumberOfQuestions(int numberPickerValue) {
        System.out.println("Number picker value is: " + numberPickerValue);
        if ((numberPickerValue * 20) > mTest.questions.size()) {
            return mTest.questions.size();
        }
        return numberPickerValue * 20;
    }

    private int setMaxNumberPickerValue() {
        int questions = mTest.questions.size();
        if (questions % 20 == 0) {
            return questions / 20;
        } else {
            return questions / 20 + 1;
        }
    }

    private void getTestResultsGraph(){
        List<TestHistory> history = mRealm.where(TestHistory.class).equalTo("testURL", mTest.url).findAllSorted("date");

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.destroyDrawingCache();
        graph.removeAllSeries();
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        LineGraphSeries<DataPoint> minPointSerie = new LineGraphSeries<>();
        minPointSerie.appendData(new DataPoint(-1, mTest.testMinPoint), true, 2);
        minPointSerie.appendData(new DataPoint(1, mTest.testMinPoint), true, 2);

        if (history.size() > 0) {

            PointsGraphSeries<DataPoint> point = new PointsGraphSeries<>();
            point.appendData(new DataPoint(0, history.get(0).points), true, 1);

            LineGraphSeries<DataPoint> serie = new LineGraphSeries<>();
            for (int i = 0; i < history.size(); i++) {
                serie.appendData(new DataPoint(i, history.get(i).points), true, history.size());
            }

            LineGraphSeries<DataPoint> minSerie = new LineGraphSeries<>();
            minSerie.appendData(new DataPoint(0, mTest.testMinPoint), true, history.size());
            minSerie.appendData(new DataPoint(history.size()-1, mTest.testMinPoint), true, history.size());

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
            paint.setColor(Color.parseColor("#79BEE0"));

            serie.setColor(Color.parseColor("#FFBB33"));
            point.setColor(Color.parseColor("#FFBB33"));
            minPointSerie.setColor(Color.parseColor("#79BEE0"));
            minSerie.setColor(Color.parseColor("#79BEE0"));
            serie.setThickness(10);
            serie.setDrawBackground(true);
            serie.setBackgroundColor(Color.argb(100, 255, 187, 51));
            serie.setDrawDataPoints(true);
            minPointSerie.setThickness(8);
            minPointSerie.setDrawBackground(false);
            minPointSerie.setDrawAsPath(true);
            minPointSerie.setCustomPaint(paint);
            minSerie.setThickness(8);
            minSerie.setDrawBackground(false);
            minSerie.setDrawAsPath(true);
            minSerie.setCustomPaint(paint);

            if (history.size() > 1){
                graph.addSeries(minSerie);
                graph.addSeries(serie);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(history.size() - 1);
            }else{
                graph.addSeries(minPointSerie);
                graph.addSeries(point);
                point.setShape(PointsGraphSeries.Shape.POINT);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(-1);
                graph.getViewport().setMaxX(1);
            }

        }else{
            graph.addSeries(minPointSerie);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(-1);
            graph.getViewport().setMaxX(1);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(mTest.testMinPoint + Math.ceil(mTest.testMinPoint / 10));
        }
    }

    @OnClick(R.id.floatingRunTest)
    public void animateMenu(){
        if(isMenuOpen){
            floatingRunTest.startAnimation(rotate_forward_90);
            runDrillButton.startAnimation(menu_close);
            runDrillButton.setClickable(false);
            runTestButton.startAnimation(menu_close);
            runTestButton.setClickable(false);
            isMenuOpen = false;
        } else {
            floatingRunTest.startAnimation(rotate_backward_90);
            runDrillButton.startAnimation(menu_open);
            runDrillButton.setClickable(true);
            runTestButton.startAnimation(menu_open);
            runTestButton.setClickable(true);
            isMenuOpen = true;
        }
    }
}
