package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.model.Answer;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    public class AnswerViewHolder extends RecyclerView.ViewHolder {

        private Answer mAnswer;

        @BindView(R.id.answer_label)
        TextView mAnswerLabel;

        @BindView(R.id.answer_radio_button)
        RadioButton mAnswerRadioButton;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Answer getAnswer() {
            return mAnswer;
        }

        public void setAnswer(Answer answer) {
            mAnswer = answer;
        }

        public void changeLabelColor(int color) {
            this.mAnswerLabel.setTextColor(color);
        }
    }

    private Context mContext;
    private List<Answer> mAnswerList;
    private int mSelectedPosition;
    private int mCorrectPosition;
    private boolean mIsAnswered;

    public AnswersAdapter(Context context, @Nullable List<Answer> answerList,
                          List<Integer> indexList, int checkedPosition,
                          boolean isAnswered) {
        mContext = context;
        mAnswerList = getShuffledAnswersList(answerList, indexList);
        mSelectedPosition = checkedPosition;
        mIsAnswered = isAnswered;
        setCorrectPosition();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public int getCorrectPosition() {
        return mCorrectPosition;
    }

    public Answer getSelectedAnswer() {
        return mAnswerList.get(mSelectedPosition);
    }

    public Answer getCorrectAnswer() {
        return mAnswerList.get(mCorrectPosition);
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AnswerViewHolder holder, final int position) {
        holder.mAnswerLabel.setText(mAnswerList.get(position).text);

        //check the radio button if both position and selectedPosition matches
        holder.mAnswerRadioButton.setChecked(position == mSelectedPosition);

        //Set the position tag to both radio button and label
        holder.mAnswerRadioButton.setTag(position);
        holder.mAnswerLabel.setTag(position);
        if (!mIsAnswered) {
            holder.mAnswerRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemCheckChanged(view);
                }
            });

            holder.mAnswerLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemCheckChanged(view);
                }


            });
        } else {
            markCorrectAndSelectedPositions(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mAnswerList ? mAnswerList.size() : 0);
    }

    private void itemCheckChanged(View view) {
        mSelectedPosition = (Integer) view.getTag();
        notifyDataSetChanged();
    }

    private void setCorrectPosition() {
        for (int i = 0; i < mAnswerList.size(); i++) {
            if (mAnswerList.get(i).correct) {
                mCorrectPosition = i;
            }
        }
    }

    public boolean isCorrectAnswer() {
        return mCorrectPosition == mSelectedPosition;
    }

    private List<Answer> getShuffledAnswersList(List<Answer> answerList, List<Integer> indexList) {
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < answerList.size(); i++) {
            answers.add(answerList.get(indexList.get(i)));
        }
        return answers;
    }

    private void markCorrectAndSelectedPositions(final AnswerViewHolder holder, final int position) {
        if (position == mCorrectPosition) {
            holder.mAnswerLabel.setTextColor(Color.GREEN);
        }
        if (mCorrectPosition != mSelectedPosition && position == mSelectedPosition) {
            holder.mAnswerLabel.setTextColor(Color.RED);
        }
    }

}
