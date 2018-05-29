package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.activity.GetTestsListActivity;
import cz.muni.fi.pv239.testmeapp.model.TestLight;


/**
 * Created by Franta on 29.03.2018.
 */

public class TestLightAdapter  extends RecyclerView.Adapter<TestLightAdapter.ViewHolder>  {

    private Context mContext;
    private List<TestLight> mTests;
    private GetTestsListActivity mActivity;

    public TestLightAdapter(@NonNull List<TestLight> tests, GetTestsListActivity mActivity) {
        mTests = tests;
        this.mActivity = mActivity;
    }

    public void refreshTests(@NonNull List<TestLight> tests) {
        mTests = tests;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_test_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TestLight test = mTests.get(position);
        holder.mFileName.setText(test.name);
        holder.setDownloadUrl(test.download_url);
    }

    @Override
    public int getItemCount() {
        return mTests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private String downloadUrl;

        @BindView(R.id.name)
        TextView mFileName;

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.downloadTestFromListButton)
        protected void downloadTestFromList(){
            mActivity.downloadTest(downloadUrl);

        }
    }

}
