package cz.muni.fi.pv239.testmeapp.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Created by Lenka on 26/03/2018.
 */

public class QuestionFragment extends Fragment {

    private int mQuestionNumber;

    @NonNull
    public int getQuestionNumber() {
        return mQuestionNumber;
    }

    public void setQuestionNumber(@NonNull int questionNumber) {
        mQuestionNumber = questionNumber;
    }

    @NonNull
    public static QuestionFragment newInstance() {
        QuestionFragment question = new QuestionFragment();
        question.setQuestionNumber(0);
        return question;
    }
}
