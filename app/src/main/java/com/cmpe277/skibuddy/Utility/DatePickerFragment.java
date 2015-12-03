package com.cmpe277.skibuddy.Utility;

import android.app.*;
import android.os.Bundle;
import android.app.DatePickerDialog.*;
import java.util.Calendar;


public  class DatePickerFragment extends DialogFragment{

    private Activity mActivity;
    private OnDateSetListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (OnDateSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(mActivity, mListener, year, month, day);
    }
}
