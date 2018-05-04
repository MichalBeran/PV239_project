package cz.muni.fi.pv239.testmeapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
    private Animation rotate_backward_90, rotate_forward_90, menu_open, menu_close;
    private boolean isMenuOpen = false;
    private Dialog mDialog;

    @BindView(R.id.testParameters)
    TextView testParameters;

    @BindView(R.id.removeTest)
    Button removeButton;

    @BindView(R.id.testName)
    TextView testName;

    @BindView(R.id.floatingRunTest)
    FloatingActionButton floatingRunTest;

    @BindView(R.id.runDrillButton)
    Button runDrillButton;

    @BindView(R.id.runTestButton)
    Button runTestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test);
        mTestApi = new TestApi();
        mUnbinder = ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        String url = getIntent().getStringExtra("url");
        mTest = mRealm.where(Test.class).equalTo("url", url).findFirst();
        testParameters.setText(getString(R.string.text_test_count) + ": " + mTest.questions.size() + "\n" +
                                getString(R.string.text_test_duration) + ": " + mTest.testDuration + "\n" +
                                getString(R.string.text_test_min_points) + ": " + mTest.testMinPoint);
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

    @Override
    protected void onPause() {
        super.onPause();
        if(isMenuOpen){
            animateMenu();
        }
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

    //@OnClick(R.id.shareQr)
    public void shareQrCode(){
        Intent intent = CreateQRCodeActivity.newIntent(this);
        intent.putExtra("qr", mTest.url);
        startActivity(intent);
    }

    @OnClick(R.id.runDrillButton)
    public void runTestDrill(){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_number_picker, null);

        final NumberPicker np = setUpNumberPicker(dialogView);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.setTitle("How many questions would you like to get?")
                .setView(dialogView)
                .setPositiveButton(R.string.text_run_drill, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startDrillTest(getCorrectNumberOfQuestions(np.getValue()));
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        mDialog.show();
    }

    @OnClick(R.id.runTestButton)
    public void runTest() {
        //TODO: implement testRunActivity
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        mDialog = builder.setTitle("Run test")
                .setMessage("Not implemented.")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        mDialog.show();
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
        if (mDialog != null) {
            mDialog.dismiss();
        }
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

    private void startDrillTest(int numberOfQuestions) {
        Intent intent = RunDrillTestActivity.newIntent(this, numberOfQuestions);
        String[] urlSplit = mTest.url.split("/");
        intent.putExtra("testFileName", urlSplit[urlSplit.length - 1]);
        intent.putExtra("testName", mTest.name);
        intent.putExtra("url", mTest.url);
        intent.putExtra("questionsLeft", 0);
        startActivity(intent);
    }

    private NumberPicker setUpNumberPicker(View dialogView) {
        NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.number_picker);

        numberPicker.setMaxValue(mTest.questions.size() / 20 + 1);
        numberPicker.setMinValue(1);
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

    private int getCorrectNumberOfQuestions(int numberPickerValue) {
        System.out.println("Number picker value is: " + numberPickerValue);
        if ((numberPickerValue * 20) > mTest.questions.size()) {
            return mTest.questions.size();
        }
        return numberPickerValue * 20;
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

    @OnClick(R.id.floatingRunTest)
    public void animateMenu(){
        if(isMenuOpen){
            floatingRunTest.startAnimation(rotate_backward_90);
            runDrillButton.startAnimation(menu_close);
            runDrillButton.setClickable(false);
            runTestButton.startAnimation(menu_close);
            runTestButton.setClickable(false);
            isMenuOpen = false;
        } else {
            floatingRunTest.startAnimation(rotate_forward_90);
            runDrillButton.startAnimation(menu_open);
            runDrillButton.setClickable(true);
            runTestButton.startAnimation(menu_open);
            runTestButton.setClickable(true);
            isMenuOpen = true;
        }
    }
}
