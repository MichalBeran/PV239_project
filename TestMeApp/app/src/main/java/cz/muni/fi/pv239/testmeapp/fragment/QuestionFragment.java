package cz.muni.fi.pv239.testmeapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.activity.ShowTestActivity;
import cz.muni.fi.pv239.testmeapp.adapter.AnswersAdapter;
import cz.muni.fi.pv239.testmeapp.model.Question;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;

/**
 * Created by Lenka on 26/03/2018.
 */

public class QuestionFragment extends Fragment {

    private int mCurrentQuestionNumber;
    private int mNumberOfQuestions;
    private ArrayList<Integer> mIndexList;
    private Question mQuestion;
    private Realm mRealm;
    private Unbinder mUnbinder;
    private AnswersAdapter mAdapter;
    private Dialog mDialog;

    @BindView(R.id.answers_view)
    RecyclerView mAnswersRecyclerView;

    @BindView(R.id.question_text)
    TextView mQuestionText;

    @BindView(R.id.test_drill_submit_button)
    Button mSubmitButton;

    @NonNull
    public int getCurrentQuestionNumber() {
        return getActivity().getIntent().getExtras().getIntegerArrayList("questionIndexes").get(mCurrentQuestionNumber);
    }

    @NonNull
    public static QuestionFragment newInstance() {
        QuestionFragment question = new QuestionFragment();
        return question;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        mCurrentQuestionNumber = getActivity().getIntent().getExtras().getInt("questionNumber");
        mNumberOfQuestions = getActivity().getIntent().getExtras().getInt("numberOfQuestions");
        System.out.println("Number of questions: " + mNumberOfQuestions);
        System.out.println("Current question:    " + mCurrentQuestionNumber);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Test test = getTest(getActivity().getIntent().getStringExtra("testName"));
        updateViewVariables(test, savedInstanceState);
        updateSubmitButtonName();
        System.out.println(
                String.format("Points gathered: %d.",
                        getActivity().getIntent().getExtras().getInt("points"))
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @OnClick(R.id.test_drill_submit_button)
    protected void submitButtonClicked() {
        getActivity().getIntent().removeExtra("checkedAnswer");
        getActivity().getIntent().putExtra("checkedAnswer", mAdapter.getSelectedPosition());
        if (mSubmitButton.getText().toString().equals(getString(R.string.text_finish))) {
            finishTest();
            return;
        }
        if (mSubmitButton.getText().toString().equals(getString(R.string.button_submit))) {
            checkAnswer();
        } else {
            nextQuestion();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("indexList", mIndexList);
    }

    private void markAnswers() {
        if (!mAdapter.isCorrectAnswer() && mAdapter.getSelectedPosition() >= 0) {
            ((AnswersAdapter.AnswerViewHolder) mAnswersRecyclerView
                    .findViewHolderForAdapterPosition(mAdapter.getSelectedPosition()))
                    .changeLabelColor(Color.RED);
        }

        ((AnswersAdapter.AnswerViewHolder) mAnswersRecyclerView
                .findViewHolderForAdapterPosition(mAdapter.getCorrectPosition()))
                .changeLabelColor(Color.GREEN);

        System.out.println("Correct position:  " + mAdapter.getCorrectPosition());
        System.out.println("Selected position: " + mAdapter.getSelectedPosition());
        System.out.println("mAnswersRecyclerV: " +
                mAnswersRecyclerView.findViewHolderForAdapterPosition(mAdapter.getCorrectPosition()));

    }

    private Test getTest(String testName) {
        return mRealm.where(Test.class).equalTo("name", testName).findFirst();
    }

    private void setShuffledListOfIndexes(Bundle bundle) {
        if (bundle != null && bundle.getIntegerArrayList("indexList") != null) {
            mIndexList = bundle.getIntegerArrayList("indexList");
        } else {
            mIndexList = new ArrayList<>();
            for (int i = 0; i < mQuestion.answers.size(); i++) {
                mIndexList.add(i);
            }
            Collections.shuffle(mIndexList);

        }
    }

    private void updateViewVariables(Test test, Bundle bundle) {

        mQuestion = test.questions.get(getCurrentQuestionNumber());
        mQuestionText.setText(mQuestion.text);

        setShuffledListOfIndexes(bundle);
        populateRecyclerView(mQuestion);
    }

    private void populateRecyclerView(Question question){
        mAnswersRecyclerView.setHasFixedSize(true);
        mAnswersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mAdapter == null) {
            mAdapter = new AnswersAdapter(
                    getContext(),
                    question.answers,
                    mIndexList,
                    getActivity().getIntent().getExtras().getInt("checkedAnswer"),
                    getActivity().getIntent().getExtras().getBoolean("answered"));
        }
        mAnswersRecyclerView.setAdapter(mAdapter);
    }

    private void updateSubmitButtonName(){
        if (!getActivity().getIntent().getExtras().getBoolean("answered")) {
            mSubmitButton.setText(R.string.button_submit);
        } else {
            mSubmitButton.setText(mCurrentQuestionNumber + 1 < mNumberOfQuestions
                    ? R.string.button_next_question
                    : R.string.text_finish);
        }
    }

    private void finishTest() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        mDialog = builder.setTitle("Finished!")
                .setMessage("Gathered points: " + getActivity().getIntent().getExtras().getInt("points"))
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        dialog.dismiss();
                    }
                })
                .create();
        mDialog.show();
    }

    private void checkAnswer() {
        System.out.println("Should put finnish: " + (mCurrentQuestionNumber + 1 == mNumberOfQuestions));
        mSubmitButton.setText(mCurrentQuestionNumber + 1 != mNumberOfQuestions
                ? R.string.button_next_question
                : R.string.text_finish);
        getActivity().getIntent().removeExtra("answered");
        getActivity().getIntent().putExtra("answered", true);
        if (mAdapter.isCorrectAnswer()) {
            increasePoints();
        }

        markAnswers();
    }

    private void increasePoints() {
        int points = getActivity().getIntent().getExtras().getInt("points");
        points += mRealm.copyFromRealm(mAdapter.getCorrectAnswer()).points;
        getActivity().getIntent().removeExtra("points");
        getActivity().getIntent().putExtra("points", points);
    }

    private void nextQuestion() {
        QuestionFragment newFragment = newInstance();

        getActivity().getIntent().removeExtra("questionNumber");
        getActivity().getIntent().putExtra("questionNumber", mCurrentQuestionNumber + 1);
        getActivity().getIntent().removeExtra("checkedAnswer");
        getActivity().getIntent().putExtra("checkedAnswer", -1);
        getActivity().getIntent().removeExtra("answered");
        getActivity().getIntent().putExtra("answered", false);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, newFragment, Question.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

}
