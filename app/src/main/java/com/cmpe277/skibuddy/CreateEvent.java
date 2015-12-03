package com.cmpe277.skibuddy;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.DatePickerFragment;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.TimePickerFragment;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.parse.*;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private SessionManager session;

    private TextView startDate;
    private TextView endDate;
    private TextView startTime;
    private TextView endTime;

    private Button dateStart;
    private Button dateEnd;
    private Button timeStart;
    private Button timeEnd;
    private Button saveEvent;

    private EditText eventName;
    private EditText eventDescription;

    boolean dateFlag = true;
    boolean timeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session =  new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            setContentView(R.layout.activity_create_event);

            eventName = (EditText) findViewById(R.id.eventTitle);
            eventDescription = (EditText) findViewById(R.id.eventDesc);

            startTime = (TextView) findViewById(R.id.startTime);
            startDate = (TextView) findViewById(R.id.startDate);
            endTime = (TextView) findViewById(R.id.endTime);
            endDate = (TextView) findViewById(R.id.endDate);


            dateStart = (Button) findViewById(R.id.dateStart);
            dateEnd = (Button) findViewById(R.id.dateEnd);
            timeStart = (Button) findViewById(R.id.timeStart);
            timeEnd = (Button) findViewById(R.id.timeEnd);
            saveEvent = (Button) findViewById(R.id.saveEvent);

            dateStart.setOnClickListener(this);
            dateEnd.setOnClickListener(this);
            timeStart.setOnClickListener(this);
            timeEnd.setOnClickListener(this);
            saveEvent.setOnClickListener(this);

        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            session.logoutUser();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.timeStart :
                timeFlag = true;
                DialogFragment startTimeFragment = new TimePickerFragment();
                startTimeFragment.show(getFragmentManager(), "timePicker");
                break;

            case R.id.dateStart :
                dateFlag = true;
                DialogFragment startDateFragment = new DatePickerFragment();
                startDateFragment.show(getFragmentManager(), "datePicker");
                break;

            case R.id.timeEnd :
                timeFlag = false;
                DialogFragment endTimeFragment = new TimePickerFragment();
                endTimeFragment.show(getFragmentManager(), "timePicker");
                break;

            case R.id.dateEnd :
                dateFlag = false;
                DialogFragment endDateFragment = new DatePickerFragment();
                endDateFragment.show(getFragmentManager(), "datePicker");
                break;

            case R.id.saveEvent :
                saveEvent();
               break;
        }
    }

    private void saveEvent(){

        String name = eventName.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String start_date = startDate.getText().toString().trim();
        String start_time = startTime.getText().toString().trim();
        String end_date = endDate.getText().toString().trim();
        String end_time = endTime.getText().toString().trim();

        if(!name.equals("") && !description.equals("") && !start_date.equals("") && !start_time.equals("") && !end_date.equals("") && !end_time.equals("")){
            Date d1 = getParsedDate(start_date, start_time);
            Date d2 = getParsedDate(end_date, end_time);

            if(d1.getTime() < d2.getTime()){
                Event  event = new Event();
                event.setName(name);
                event.setDescription(description);
                event.setStartDate(d1);
                event.setStartTime(start_time);
                event.setEndDate(d2);
                event.setEndTime(end_time);

                final ParseObject eventObj = new ParseObject("Event");
                eventObj.put("eventName", event.getName());
                eventObj.put("description", event.getDescription());
                eventObj.put("startDate", event.getStartDate());
                eventObj.put("endDate", event.getEndDate());
                eventObj.put("startTime", event.getStartTime());
                eventObj.put("endTime", event.getEndTime());

                eventObj.saveInBackground();

                eventObj.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            String id = eventObj.getObjectId();
                            createGroup(id);
                        } else {
                            Utilities.shortMsg(getApplicationContext(), "Error occurred in event creation");
                        }
                    }

                });

                Log.d(Constatnts.TAG, event.toString());
            }else
                Utilities.longMsg(getApplicationContext(), "Start date can not occur later then end date.");

        }else
            Utilities.shortMsg(getApplicationContext(), "All fields are mandatory");
    }

    private void createGroup(final String eventId){
        String userId = session.getLoggedInMail();

        ParseObject group = new ParseObject("Groups");
        group.put("userId", userId);
        group.put("eventId", eventId);
        group.put("flag", "1");
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                   Utilities.shortMsg(getApplicationContext(), "Event created successfully");
                    finish();
                } else {
                    Utilities.shortMsg(getApplicationContext(), "Error occurred in event creation");
                }
            }
        });

    }

    private Date getParsedDate(String date, String time){
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date newDate = null;
        try {
            Date d = df.parse(date);

            String[] array = time.split(":");

            Calendar calendar=Calendar.getInstance();
            calendar.setTime(d);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(array[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(array[1]));
            newDate = calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  newDate;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay+":"+minute;
        if(timeFlag)
            startTime.setText(time);
        else
            endTime.setText(time);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
        if(dateFlag)
            startDate.setText(date);
        else
            endDate.setText(date);
    }
}
