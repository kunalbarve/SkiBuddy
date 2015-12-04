package com.cmpe277.skibuddy.Utility;

import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    public static  String getDateString(Date date){
        String result = "";

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        result = df.format(date);
        return result;
    }

    public static Date getCurrentDate(){
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        Date newDate = null;
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeZone(timeZone);
            calendar.setTime(new Date());
            newDate = calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  newDate;
    }
}
