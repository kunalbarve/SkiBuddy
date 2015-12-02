package com.cmpe277.skibuddy.Utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by knbarve on 12/1/15.
 */
public class Utilities {

    public static void shortMsg(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void longMsg(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
