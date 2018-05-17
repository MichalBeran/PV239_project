package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;
import cz.muni.fi.pv239.testmeapp.fragment.QuestionFragment;
import cz.muni.fi.pv239.testmeapp.model.Question;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;

/**
 * Created by Lenka on 26/03/2018.
 */

public class RunDrillTestActivity extends FragmentActivity {

    protected List<Integer> mQuestionsIndexes;
    protected Realm mRealm;

    @NonNull
    public static Intent newIntent(@NonNull Context context, int questions) {
        Intent intent = new Intent(context, RunDrillTestActivity.class);
        intent.putExtra("numberOfQuestions", questions);
        Log.i("questions", "Number of requested questions is " + questions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestMeApp.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_drill_test);
        mRealm = Realm.getDefaultInstance();
        shuffleTestQuestions();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            getIntent().putIntegerArrayListExtra("questionIndexes", (ArrayList<Integer>) mQuestionsIndexes);
            getIntent().putExtra("points", 0);
            getIntent().putExtra("questionNumber", 0);
            getIntent().putExtra("checkedAnswer", -1);
            getIntent().putExtra("answered", false);
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content,
                            QuestionFragment.newInstance(),
                            QuestionFragment.class.getSimpleName())
                    .commit();
        } else {
            fragmentManager.findFragmentByTag(QuestionFragment.class.getSimpleName());
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_quit_drill_title)
                .setMessage(R.string.text_quit_drill_message).setPositiveButton(R.string.text_yes, dialogClickListener)
                .setNegativeButton(R.string.text_no, dialogClickListener).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    protected void shuffleTestQuestions() {
        Test test = mRealm.where(Test.class)
                .equalTo("name", this.getIntent().getStringExtra("testName"))
                .findFirst();
        mQuestionsIndexes = getTestQuestionIndexesShuffled(test.questions);
    }

    protected List<Integer> getTestQuestionIndexesShuffled(List<Question> questions) {
        List<Integer> helperList = new ArrayList<>();
        for(int i = 0; i < questions.size(); i++) {
            helperList.add(i);
        }

        Collections.shuffle(helperList);
        return helperList;
    }
}
