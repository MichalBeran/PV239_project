package cz.muni.fi.pv239.testmeapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.api.TestApi;
import cz.muni.fi.pv239.testmeapp.model.Test;
import cz.muni.fi.pv239.testmeapp.model.TestLight;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Franta on 29.03.2018.
 */

public class TestLightAdapter  extends RecyclerView.Adapter<TestLightAdapter.ViewHolder>  {

    private Context mContext;
    private List<TestLight> mTests;

    public TestLightAdapter(@NonNull List<TestLight> tests) {
        mTests = tests;
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

        @BindView(R.id.loadingPanel)
        ProgressBar mLoadingBar;

        @BindView(R.id.downloadTestFromListButton)
        Button downloadButton;

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
            dowloadByHolder();
        }

        private void dowloadByHolder(){
            final TestApi mTestApi = new TestApi();
            final String path = Uri.parse(downloadUrl).getPath();
            final Call<Test> testCall = mTestApi.getService().getTest(path);

            mLoadingBar.setVisibility(View.VISIBLE);
            downloadButton.setVisibility(View.GONE);

            testCall.enqueue(new Callback<Test>() {

                @Override
                public void onResponse(Call<Test> call, retrofit2.Response<Test> response) {
                    if (response.code() == 404 || response.code() == 400){
                        // FAILURE - 404 or 400
                        mLoadingBar.setVisibility(View.GONE);
                        downloadButton.setText(mContext.getString(R.string.test_download_failed) + " - " + mContext.getString(R.string.retry));
                        downloadButton.setVisibility(View.VISIBLE);
                    }else {
                        Test test = response.body();
                        if (test == null) {
                            return;
                        }
                        test.url = mTestApi.getUrlBase() + path;
                        test.favourite = isFavouriteTest(test.url);
                        Boolean state = saveResult(test);
                        // OK state
                        if(state){
                            mLoadingBar.setVisibility(View.GONE);
                            downloadButton.setText(mContext.getString(R.string.test_save_successful));
                            downloadButton.setClickable(false);
                            downloadButton.setVisibility(View.VISIBLE);
                        }else{
                            mLoadingBar.setVisibility(View.GONE);
                            downloadButton.setText(mContext.getString(R.string.test_download_failed) + " - " + mContext.getString(R.string.retry));
                            downloadButton.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Test> call, Throwable t) {
                    // FAILURE
                    t.printStackTrace();
                    mLoadingBar.setVisibility(View.GONE);
                    downloadButton.setText(mContext.getString(R.string.test_download_failed) + " - " + mContext.getString(R.string.retry));
                    downloadButton.setVisibility(View.VISIBLE);
                }
            });
        }

        private Boolean saveResult(final Test test) {
            Realm realm = null;
            Boolean state = false;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insertOrUpdate(test);
                    }
                });
                state = true;
            } finally {
                if(realm != null) {
                    realm.close();
                }
            }
            return state;
        }

        private boolean isFavouriteTest(String url) {
            Realm realm = null;
            Test test = null;
            try {
                realm = Realm.getDefaultInstance();
                test = realm.where(Test.class)
                        .equalTo("url", url)
                        .findFirst();

            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
            if (test != null) {
                return test.favourite;
            }
            return false;
        }
    }

}
