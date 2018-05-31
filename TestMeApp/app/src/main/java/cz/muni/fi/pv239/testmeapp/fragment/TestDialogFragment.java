package cz.muni.fi.pv239.testmeapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Timer;
import java.util.TimerTask;

import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.activity.GetTestActivity;
import cz.muni.fi.pv239.testmeapp.activity.ShowTestActivity;

public class TestDialogFragment extends DialogFragment {

    private static int mType;

    private int RETRY_DIALOG = 1;
    private int FAILURE_404_DIALOG = 2;
    private int SUCCESS_DIALOG = 3;
    private int DOWNLOAD_PROGRESS_DIALOG = 4;
    private int QUIT_TEST_DIALOG = 5;
    private int QUIT_DRILL_DIALOG = 6;
    private int FINISH_TEST_DIALOG = 7;
    private int DRILL_NUMBER_PICKER = 8;
    private int REMOVE_TEST_DIALOG = 9;

    public static TestDialogFragment newInstance(int type) {
        TestDialogFragment frag = new TestDialogFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        frag.setArguments(args);
        mType = type;
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("type", mType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int type = getArguments().getInt("type");

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        final Dialog dialog;
        if (type == RETRY_DIALOG){
            builder.setTitle(R.string.test_download_failed)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GetTestActivity act = (GetTestActivity)getActivity();
                            act.loadTest(act.mUrlText.getText() + "");
                        }
                    })
                    .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
        }
        else if (type == FAILURE_404_DIALOG){
            builder.setTitle(R.string.test_download_failed_bad_response)
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            dialog = builder.create();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    t.cancel();
                }
            }, 2000);
        }
        else if (type == SUCCESS_DIALOG){
            builder.setTitle(R.string.test_save_successful)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    t.cancel();
                }
            }, 3000);
        }else if(type == DOWNLOAD_PROGRESS_DIALOG){
            final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(getString(R.string.test_downloading));
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);

            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetTestActivity act = (GetTestActivity)getActivity();
                    act.CancelTestCall();
                    dialog.dismiss();
                }
            });
            dialog = mProgressDialog;
        }
        else if(type == QUIT_TEST_DIALOG){
            builder.setTitle(R.string.text_quit_test_title)
                    .setMessage(R.string.text_quit_test_message)
                    .setPositiveButton(R.string.text_yes,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
        }else if(type == QUIT_DRILL_DIALOG){
            builder.setTitle(R.string.text_quit_drill_title)
                    .setMessage(R.string.text_quit_drill_message)
                    .setPositiveButton(R.string.text_yes,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
        }else if(type == FINISH_TEST_DIALOG){
            builder.setTitle(R.string.text_test_finished)
                    .setMessage(getString(R.string.text_gathered_points) + ": " + getActivity().getIntent().getExtras().getInt("points"))
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                            getActivity().finish();
                        }
                    })
                    .setCancelable(false);
            dialog = builder.create();
        }else if(type == DRILL_NUMBER_PICKER){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.item_number_picker, null);


            final ShowTestActivity act = (ShowTestActivity)getActivity();
            final NumberPicker np = act.setUpNumberPicker(dialogView);

            builder.setTitle(R.string.how_many_questions)
                    .setView(dialogView)
                    .setPositiveButton(R.string.text_run_drill, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                            if (np.getValue() > 0) {
                                act.startDrillTest(act.getCorrectNumberOfQuestions(np.getValue()));
                            }
                        }
                    })
                    .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
            dialog = builder.create();
        }else if(type == REMOVE_TEST_DIALOG){
            builder.setTitle(R.string.are_you_sure_delete)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShowTestActivity act = (ShowTestActivity)getActivity();
                            act.removeThisTest();
                            getActivity().finish();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
        }
        else {
            dialog = builder.create();
        }

        return dialog;
    }

    public boolean isShowing(){
//        return mDialog.isShowing();
        return super.getDialog().isShowing();
    }
//
//    public Dialog getDialog() {
//        return mDialog;
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        mDialog.dismiss();
//        super.onDismiss(dialog);
//    }
}
