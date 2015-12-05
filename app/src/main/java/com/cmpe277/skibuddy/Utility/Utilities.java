package com.cmpe277.skibuddy.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
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

    public void createDialogForMultipleCheckboxes(final Activity youractivity){
        final ArrayList<String> selectedItems=new ArrayList<String>();
        final CharSequence[] items = { "Gujrat", "Rajasthan",
                "Maharastra", "Panjab", "Madhya Pradesh", "Hariyana",
                "Bihar" };
        AlertDialog.Builder builder = new AlertDialog.Builder(youractivity);
        builder.setTitle("Select State");

        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which, boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            Toast.makeText(youractivity.getApplicationContext(),
                                    items[which], Toast.LENGTH_SHORT)
                                    .show();
                            selectedItems.add(items[which].toString());
                        } else if (selectedItems.contains(items[which])) {
                            selectedItems.remove(items[which].toString());
                        }


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
