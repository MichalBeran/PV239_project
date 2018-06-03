package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.fragment.QuestionFragment;
import cz.muni.fi.pv239.testmeapp.fragment.TestDialogFragment;
import cz.muni.fi.pv239.testmeapp.model.Question;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;

public class RunTestActivity extends FragmentActivity {

    private List<Integer> mQuestionsIndexes;
    private Realm mRealm;
    private Unbinder mUnbinder;
    private CountDownTimer mTimer;
    private long mRemainingTime;

    @BindView(R.id.runTestTimer)
    TextView mTimerText;

    @NonNull
    public static Intent newIntent(@NonNull Context context, int questions) {
        Intent intent = new Intent(context, RunTestActivity.class);
        intent.putExtra("numberOfQuestions", questions);
        Log.i("questions", "Number of requested questions is " + questions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        mRealm = Realm.getDefaultInstance();
        mUnbinder = ButterKnife.bind(this);
        shuffleTestQuestions();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            getIntent().putIntegerArrayListExtra("questionIndexes", (ArrayList<Integer>) mQuestionsIndexes);
            getIntent().putExtra("points", 0);
            getIntent().putExtra("questionNumber", 0);
            getIntent().putExtra("checkedAnswer", -1);
            getIntent().putExtra("answered", false);
            fragmentManager.beginTransaction()
                    .replace(R.id.runTestFragment,
                            QuestionFragment.newInstance(),
                            QuestionFragment.class.getSimpleName())
                    .commit();
        } else {
            fragmentManager.findFragmentByTag(QuestionFragment.class.getSimpleName());
        }

        long timer = 0;
        if(savedInstanceState != null) {
            timer = savedInstanceState.getLong("remainingTime");
        }else {
            Date time = stringToDate(getTest().testDuration, "hh:mm:ss");
            if (time == null) {
                time = stringToDate(getTest().testDuration, "mm:ss");
                if (time == null) {
                    time = stringToDate(getTest().testDuration, "ss");
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            timer = ((cal.get(Calendar.HOUR) * 3600) + (cal.get(Calendar.MINUTE) * 60) + cal.get(Calendar.SECOND)) * 1000;

        }
        mTimer = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished/1000) / 3600;
                int minutes = (int) ((millisUntilFinished/1000) - (hours * 3600))/60;
                int seconds = (int) ((millisUntilFinished/1000) - (hours * 3600) - (minutes *60));

                String sHours = Integer.toString(hours);
                String sMinutes = Integer.toString(minutes);
                String sSeconds = Integer.toString(seconds);

                if (hours < 10) sHours = "0" + sHours;
                if (minutes < 10) sMinutes = "0" + sMinutes;
                if (seconds < 10) sSeconds = "0" + sSeconds;

                mTimerText.setText(getString(R.string.text_remaining) + ": " + sHours + ":" + sMinutes + ":" + sSeconds);
                mRemainingTime = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                QuestionFragment frag = (QuestionFragment) fragmentManager.findFragmentById(R.id.runTestFragment);
                if(frag != null) {
                    frag.finishTest();
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("mQuitTestDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        TestDialogFragment mDialog = TestDialogFragment.newInstance(TestDialogFragment.QUIT_TEST_DIALOG);
        mDialog.onCreate(mDialog.getArguments());
        ft.add(mDialog, "mQuitTestDialog");
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // V Activity neni potreba ukladat unbinder a unbindovat
        mUnbinder.unbind();
        mRealm.close();
        mTimer.cancel();
    }

    private void shuffleTestQuestions() {
        Test test = mRealm.where(Test.class)
                .equalTo("name", this.getIntent().getStringExtra("testName"))
                .findFirst();
        mQuestionsIndexes = getTestQuestionIndexesShuffled(test.questions);
    }

    private List<Integer> getTestQuestionIndexesShuffled(List<Question> questions) {
        List<Integer> helperList = new ArrayList<>();
        for(int i = 0; i < questions.size(); i++) {
            helperList.add(i);
        }

        Collections.shuffle(helperList);
        return helperList;
    }

    private Test getTest() {
        return mRealm.where(Test.class).equalTo("name", this.getIntent().getStringExtra("testName")).findFirst();
    }

    private Date stringToDate(String aDate, String aFormat) {
        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("remainingTime", mRemainingTime);
    }
}
