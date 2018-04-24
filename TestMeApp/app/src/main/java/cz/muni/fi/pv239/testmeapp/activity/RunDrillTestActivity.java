package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.fragment.QuestionFragment;

/**
 * Created by Lenka on 26/03/2018.
 */

public class RunDrillTestActivity extends AppCompatActivity {

    public static int questions;

    @NonNull
    public static Intent newIntent(@NonNull Context context, int questions) {
        Intent intent = new Intent(context, RunDrillTestActivity.class);
        RunDrillTestActivity.questions = questions;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_drill_test);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager != null) {
                Random random = new Random();
                getIntent().putExtra("points", 0);
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content,
                                QuestionFragment.newInstance(random.nextInt(RunDrillTestActivity.questions)),
                                QuestionFragment.class.getSimpleName())
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Intent intent = ShowTestActivity.newIntent(this);
        final String url = getIntent().getStringExtra("url");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        intent.putExtra("url", url);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_quit_drill_title)
                .setMessage(R.string.text_quit_drill_message).setPositiveButton(R.string.text_yes, dialogClickListener)
                .setNegativeButton(R.string.text_no, dialogClickListener).show();
    }
}
