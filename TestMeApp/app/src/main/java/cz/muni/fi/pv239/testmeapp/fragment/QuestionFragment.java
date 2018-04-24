package cz.muni.fi.pv239.testmeapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.activity.RunDrillTestActivity;
import cz.muni.fi.pv239.testmeapp.activity.ShowTestActivity;
import cz.muni.fi.pv239.testmeapp.adapter.AnswersAdapter;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Answer;
import cz.muni.fi.pv239.testmeapp.model.Question;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenka on 26/03/2018.
 */

public class QuestionFragment extends Fragment {

    private int mQuestionNumber;
    private TestApi mTestApi;
    private Question mQuestion;
    private Realm mRealm;
    private Unbinder mUnbinder;
    private AnswersAdapter mAdapter;

    @BindView(R.id.answers_view)
    RecyclerView mAnswersRecyclerView;

    @BindView(R.id.question_text)
    TextView mQuestionText;

    @BindView(R.id.test_drill_submit_button)
    Button mSubmitButton;

    @NonNull
    public int getQuestionNumber() {
        return mQuestionNumber;
    }

    public void setQuestionNumber(@NonNull int questionNumber) {
        mQuestionNumber = questionNumber;
    }

    @NonNull
    public static QuestionFragment newInstance(int questionNumber) {
        QuestionFragment question = new QuestionFragment();
        question.setQuestionNumber(questionNumber);
        return question;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTestApi = new TestApi();
        mRealm = Realm.getDefaultInstance();
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
        loadDownloadedTest(getActivity().getIntent().getStringExtra("testName"));
        System.out.println(
                String.format("Points gathered: %d.\nQuestions answered/remained: %d/%d",
                        getActivity().getIntent().getExtras().getInt("points"),
                        mQuestionNumber, RunDrillTestActivity.questions)
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
        if (mSubmitButton.getText().toString().equals(getString(R.string.button_submit))) {
            checkAnswer();
        } else {
            submitAnswer();
        }
    }

    private void checkAnswer() {
        mSubmitButton.setText(R.string.button_next_question);
        if (mAdapter.isCorrectAnswer()) {
            ((AnswersAdapter.AnswerViewHolder)
                    mAnswersRecyclerView
                            .findViewHolderForAdapterPosition(mAdapter.getSelectedPosition()))
                    .changeLabelColor(Color.GREEN);
            increasePoints();
        } else {
            if(mAdapter.getSelectedPosition() >= 0) {
                ((AnswersAdapter.AnswerViewHolder) mAnswersRecyclerView
                        .findViewHolderForAdapterPosition(mAdapter.getSelectedPosition()))
                        .changeLabelColor(Color.RED);
            }

            ((AnswersAdapter.AnswerViewHolder) mAnswersRecyclerView
                    .findViewHolderForAdapterPosition(mAdapter.getCorrectPosition()))
                    .changeLabelColor(Color.GREEN);
        }
    }

    private void submitAnswer() {
        if (mQuestionNumber + 1 >= RunDrillTestActivity.questions) {
            Snackbar.make(getActivity().findViewById(R.id.runDrillTestFragmentContainer), R.string.drill_test_finished, Snackbar.LENGTH_LONG).show();
            Intent intent = ShowTestActivity.newIntent(getContext());
            intent.putExtra("url",
                    getTest(getActivity().getIntent().getStringExtra("testName")).url);
            startActivity(intent);
        } else {
            nextQuestion();
        }
    }

    private void increasePoints() {
        int points = getActivity().getIntent().getExtras().getInt("points");
        points += mRealm.copyFromRealm(mAdapter.getCorrectAnswer()).points;
        getActivity().getIntent().removeExtra("points");
        getActivity().getIntent().putExtra("points", points);
    }

    private void loadDownloadedTest(@NonNull final String testName) {
        Test test = getTest(testName);
        updateViewVariables(test);
    }

    private Test getTest(String testName) {
        return mRealm.where(Test.class).equalTo("name", testName).findFirst();
    }

    private void updateViewVariables(Test test) {
        mQuestion = test.questions.get(getQuestionNumber());
        mQuestionText.setText(mQuestion.text);

        populateRecyclerView(mQuestion);
    }

    private void populateRecyclerView(Question question){
        mAnswersRecyclerView.setHasFixedSize(true);
        mAnswersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AnswersAdapter(getContext(), question.answers);
        mAnswersRecyclerView.setAdapter(mAdapter);
    }

    private void nextQuestion() {
        //TODO: mQuestionNumber should be random
        //TODO: should check already answered questions, so that we don't display one question too many times
        QuestionFragment newFragment = newInstance(mQuestionNumber + 1);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit();
    }

}
