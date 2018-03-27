package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.fragment.QuestionFragment;

/**
 * Created by Lenka on 26/03/2018.
 */

public class RunDrillTestActivity extends AppCompatActivity {



    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, RunDrillTestActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_drill_test);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content,
                                QuestionFragment.newInstance(),
                                QuestionFragment.class.getSimpleName())
                        .commit();
            }
        }
    }
}
