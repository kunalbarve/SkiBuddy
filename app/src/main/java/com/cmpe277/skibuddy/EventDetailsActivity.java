package com.cmpe277.skibuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager session;
    private Context context;
    private Event event;

    private Button subscribeButton;
    private Button unSubscribeButton;
    private Button addParticipantButton;
    private Button skiTrackerButton;
    private TextView eventName;
    private TextView eventDescription;
    private TextView startTime;
    private TextView endTime;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        session = new SessionManager(context);

        if(session.checkLogin()){
            setContentView(R.layout.activity_event_details);

            subscribeButton = (Button) findViewById(R.id.subscribeButton);
            unSubscribeButton = (Button) findViewById(R.id.unSubscribeButton);
            addParticipantButton = (Button) findViewById(R.id.addParticipantButton);
            skiTrackerButton = (Button) findViewById(R.id.skiTrackerButton);
            eventName = (TextView) findViewById(R.id.textEventName);
            eventDescription = (TextView) findViewById(R.id.textEventDescription);
            startTime = (TextView) findViewById(R.id.textStartTime);
            endTime = (TextView) findViewById(R.id.textEndTime);

            subscribeButton.setOnClickListener(this);
            unSubscribeButton.setOnClickListener(this);
            addParticipantButton.setOnClickListener(this);
            skiTrackerButton.setOnClickListener(this);

            subscribeButton.setVisibility(View.INVISIBLE);
            unSubscribeButton.setVisibility(View.INVISIBLE);
            addParticipantButton.setVisibility(View.INVISIBLE);
            skiTrackerButton.setVisibility(View.INVISIBLE);

            Bundle bundle = this.getIntent().getExtras();
            if(bundle==null){
                return;
            }
            event = (Event)bundle.getSerializable("event");

            populateEventDetails(event);
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

    private void populateEventDetails(Event event) {
        if(event != null){
            String mode = event.getMode();

            if(mode.equalsIgnoreCase(Constatnts.ACTIVATED_MODE)){
                skiTrackerButton.setVisibility(View.VISIBLE);
            }else if(mode.equalsIgnoreCase(Constatnts.JOINED_MODE)){
                unSubscribeButton.setVisibility(View.VISIBLE);
                addParticipantButton.setVisibility(View.VISIBLE);
            }else if(mode.equalsIgnoreCase(Constatnts.INVITE_MODE)){
                subscribeButton.setVisibility(View.VISIBLE);
            }

            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
            startTime.setText((CharSequence) event.getStartDate());
            endTime.setText((CharSequence) event.getEndDate());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeButton:

                break;

            case R.id.unSubscribeButton:
                break;

            case R.id.addParticipantButton:
                break;

            case R.id.skiTrackerButton:
                break;
        }
    }
}


