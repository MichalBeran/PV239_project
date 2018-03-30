package cz.muni.fi.pv239.testmeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import cz.muni.fi.pv239.testmeapp.activity.CreateQRCodeActivity;
import cz.muni.fi.pv239.testmeapp.activity.ShowTestActivity;
import cz.muni.fi.pv239.testmeapp.adapter.AnswersAdapter;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
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

    @BindView(R.id.answer_submit_button)
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

    @OnClick(R.id.answer_submit_button)
    protected void submitAnswer() {
        if (mQuestionNumber + 1 >= mQuestion.answers.size()) {
            Toast.makeText(getContext(), "Test finnished!", Toast.LENGTH_SHORT).show();
            Intent intent = ShowTestActivity.newIntent(getContext());
            intent.putExtra("url",
                    getTest(getActivity().getIntent().getStringExtra("testName")).url);
            startActivity(intent);
        } else {
            setQuestionNumber(mQuestionNumber + 1);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadTestOnline(@NonNull final String testName) {
        Call<Test> testCall = mTestApi.getService().getTest(testName);

        testCall.enqueue(new Callback<Test>() {
            @Override
            public void onResponse(Call<Test> call, Response<Test> response) {
                updateViewVariables(response.body());
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Could not load test", Toast.LENGTH_SHORT).show();
            }
        });
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

}
