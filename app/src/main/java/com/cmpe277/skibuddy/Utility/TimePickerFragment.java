package com.cmpe277.skibuddy.Utility;

import android.app.*;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.app.TimePickerDialog.*;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private Activity mActivity;
    private OnTimeSetListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(mActivity, mListener, hour, minute,
                DateFormat.is24HourFormat(mActivity));
    }
}