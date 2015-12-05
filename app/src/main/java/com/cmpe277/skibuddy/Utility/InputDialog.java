package com.cmpe277.skibuddy.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.EditText;


public class InputDialog extends DialogFragment{

    private Activity mActivity;
    private EditText input;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(String value);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        input = new EditText(mActivity);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setView(input).setTitle("Add Participant")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String value = input.getText().toString().trim();
                        mListener.onDialogPositiveClick(value);
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }

}
