package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

import cz.muni.fi.pv239.testmeapp.R;

/**
 * Created by Michal on 21.03.2018.
 */

public class TestsAdapter extends RealmRecyclerViewAdapter<Test, TestsAdapter.ViewHolder> {

    private Context mContext;

    public TestsAdapter(Context context, @Nullable OrderedRealmCollection<Test> data) {
        super(data, true);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Test test = getItem(position);
        holder.mName.setText(test.name);
        holder.mDuration.setText("Duration: " + test.testDuration);
        holder.mUrl.setText(test.url);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView mName;

        @BindView(R.id.duration)
        TextView mDuration;

        @BindView(R.id.url)
        TextView mUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
