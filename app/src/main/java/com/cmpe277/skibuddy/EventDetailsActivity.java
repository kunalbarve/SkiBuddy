package com.cmpe277.skibuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.GroupDao;
import com.cmpe277.skibuddy.DAOs.UserDao;
import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.ListUtility.UserAdapter;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ListView listView;
    
    private ArrayList<User> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        session = new SessionManager(context);

        if(session.checkLogin()){
            setContentView(R.layout.activity_event_details);

            listView = (ListView) findViewById(R.id.participantsListView);

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
            startTime.setText(Utilities.getDateString(event.getStartDate()));
            endTime.setText(Utilities.getDateString(event.getEndDate()));

            getGroupDetails(event.getId());
        }
    }

    private void getGroupDetails(String eventId) {
        GroupDao.getGroupDetailsForEvents(eventId, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("event_group_callback");
                    List<Group> groupDetails = callback.getGroupDetails();
                    Log.d(Constatnts.TAG, "" + groupDetails.size());

                    List<String> userIds = new ArrayList<String>();
                    for (Group group : groupDetails){
                        userIds.add(group.getUserId());
                    }

                    getUserDetails(userIds);
                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }

    private void getUserDetails(List<String> userIds) {
        UserDao.getUserDetails(userIds, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("user_callback");
                    userDetails = callback.getUserDetails();
                    Log.d(Constatnts.TAG, "" + userDetails.size());

                    UserAdapter adapter = new UserAdapter(context, userDetails, event);
                    listView.setAdapter(adapter);
                    ListUtils.setDynamicHeight(listView);

                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
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
                Bundle extras = new Bundle();
                extras.putSerializable("event", event);

                Intent skiTrackerIntent = new Intent(context, SkiTrackerActivity.class);
                skiTrackerIntent.putExtras(extras);
                startActivity(skiTrackerIntent);
                break;
        }
    }
}


