package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.model.TestHistory;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class HistoryAdapter extends RealmRecyclerViewAdapter<TestHistory, HistoryAdapter.ViewHolder> {
    private Context mContext;

    public HistoryAdapter(Context context, @Nullable OrderedRealmCollection<TestHistory> data) {
        super(data, true);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new HistoryAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TestHistory history = getItem(position);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy  HH:mm");
        String date = format.format(history.date);
        holder.mDate.setText(date);
        String points = history.points + "";
        holder.mPoints.setText(mContext.getString(R.string.text_gathered_points) + ": " + points);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.itemTestHistoryDate)
        TextView mDate;

        @BindView(R.id.itemTestHistoryPoints)
        TextView mPoints;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
