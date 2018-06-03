package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.activity.ShowTestActivity;
import cz.muni.fi.pv239.testmeapp.model.Test;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * AndroidStudio bohuzel jako default nechava tento JavaDoc, ale realne je to zbytecne pridani 3 radku do kodu, ktery pak clovek musi o neco vic odscrollovat.
 * Tyto informace jsou v Gitu, neni potreba delat JavaDoc s redundantnimi informacemi.
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
        holder.setUrl(test.url);
        holder.mName.setText(test.name);
        holder.mDuration.setText(mContext.getString(R.string.text_test_duration) + ": " + test.testDuration);
        holder.mCount.setText(mContext.getString(R.string.text_test_count) + ": " + test.testCount);
        if (test.favourite) {
            holder.favStar.setColorFilter(ContextCompat.getColor(mContext, R.color.colorFavouriteYellow));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private String mUrl;

        @BindView(R.id.itemTestName)
        TextView mName;

        @BindView(R.id.itemTestDuration)
        TextView mDuration;

        @BindView(R.id.itemTestCount)
        TextView mCount;

        @BindView(R.id.favouriteStar)
        ImageView favStar;

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            this.mUrl = url;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.showTestButton)
        public void setShowTest(){
            link();
        }

        @OnClick
        public void link(){
            Intent intent = ShowTestActivity.newIntent(mContext);
            intent.putExtra("url", mUrl);
            mContext.startActivity(intent);
        }

    }
}
