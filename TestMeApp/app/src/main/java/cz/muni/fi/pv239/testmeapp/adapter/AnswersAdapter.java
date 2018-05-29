package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.model.Answer;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    public class AnswerViewHolder extends RecyclerView.ViewHolder {

        private Answer mAnswer;

        @BindView(R.id.answer_item)
        LinearLayout mAnswerItem;

        @BindView(R.id.answer_label)
        TextView mAnswerLabel;

        @BindView(R.id.answer_radio_button)
        RadioButton mAnswerRadioButton;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(3, mContext.getResources().getColor(R.color.colorFavouriteGray));
            drawable.setCornerRadius(5);
            mAnswerItem.setBackground(drawable);
        }

        public Answer getAnswer() {
            return mAnswer;
        }

        public void setAnswer(Answer answer) {
            mAnswer = answer;
        }

        public void changeLabelColor(int color) {
            this.mAnswerLabel.setTextColor(color);
            setBorder(color, 6, 8, mAnswerItem);
        }

        @OnClick
        public void click(){
            if (!mIsAnswered && itemView!=null) {
                if (itemView.getTag() != null){
                    itemCheckChanged(this.itemView);
                    System.out.println("answered " + mIsAnswered);
                }
            }
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
        holder.mAnswerItem.setTag(position);
        if (!mIsAnswered) {
            holder.mAnswerRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mIsAnswered) {
                        itemCheckChanged(view);
                    }
                }
            });

            holder.mAnswerLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mIsAnswered) {
                        itemCheckChanged(view);
                    }
                }
            });
            if(mSelectedPosition == position){
                setBorder(mContext.getResources().getColor(R.color.colorAccent), 6, 8, holder.mAnswerItem);
            }else{
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = mContext.getTheme();
                theme.resolveAttribute(R.attr.colorText, typedValue, true);
                int textColor = typedValue.data;
                setBorder(textColor, 6, 8, holder.mAnswerItem);
            }
        } else {
            markCorrectAndSelectedPositions(holder, position);
            holder.mAnswerRadioButton.setClickable(false);
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
            holder.mAnswerLabel.setTextColor(mContext.getResources().getColor(R.color.colorTextRight));
            holder.changeLabelColor(mContext.getResources().getColor(R.color.colorTextRight));
        }
        if (mCorrectPosition != mSelectedPosition && position == mSelectedPosition) {
            holder.mAnswerLabel.setTextColor(mContext.getResources().getColor(R.color.colorTextWrong));
            holder.changeLabelColor(mContext.getResources().getColor(R.color.colorTextWrong));
        }
    }

    private void setBorder(int color, int width, int radius, LinearLayout layout){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(width, color);
        drawable.setCornerRadius(radius);
        layout.setBackground(drawable);
    }

    public void setAsAnswered(){
        this.mIsAnswered = true;
        notifyDataSetChanged();
    }

}
