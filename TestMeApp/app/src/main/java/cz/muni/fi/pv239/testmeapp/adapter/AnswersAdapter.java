package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.model.Answer;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    class AnswerViewHolder extends RecyclerView.ViewHolder {

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
    }

    private Context mContext;
    private List<Answer> mAnswerList;
    private int mSelectedPosition = -1;

    public AnswersAdapter(Context context, @Nullable List<Answer> answerList) {
        mContext = context;
        mAnswerList = answerList;
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
    }

    @Override
    public int getItemCount() {
        return (null != mAnswerList ? mAnswerList.size() : 0);
    }

    private void itemCheckChanged(View view) {
        mSelectedPosition = (Integer) view.getTag();
        notifyDataSetChanged();
    }



}
