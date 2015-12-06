package com.cmpe277.skibuddy;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.cmpe277.skibuddy.Utility.InputDialog;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.TimePickerFragment;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class EventDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, InputDialog.NoticeDialogListener {

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

    //get user location if event is active one
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final static int GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER = 8787;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        session = new SessionManager(context);

        if(session.checkLogin()){
            setContentView(R.layout.activity_event_details);

            Bundle bundle = this.getIntent().getExtras();
            if(bundle==null  ||  (bundle.getSerializable("event") == null)){
                return;
            }
            event = (Event)bundle.getSerializable("event");

            listView = (ListView) findViewById(R.id.participantsListView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                    User user = userDetails.get(pos);
                    Bundle extras = new Bundle();
                    extras.putSerializable("event", event);
                    extras.putSerializable("user", user);
                    Intent userDetailsIntent = new Intent(context, UserDetailsActivity.class);
                    userDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    userDetailsIntent.putExtras(extras);
                    context.startActivity(userDetailsIntent);
                }
            });

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

            populateEventDetails(event);

            if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }else{
            finish();
        }

        setTitle("Explore Events");
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
        }else if(id == R.id.updateProfile){
            String url = session.getLoggedInUserDetails().getUrl();
            if(url.equals(""))
                Utilities.shortMsg(getApplicationContext(), "User profile information missing. Try later!");
            else{
                session.logoutUser();
                finish();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
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
                    for (Group group : groupDetails) {
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
                updateGroupDetails(Constatnts.SUBSCRIBE_MODE);
                break;

            case R.id.unSubscribeButton:
                updateGroupDetails(Constatnts.UN_SUBSCRIBE_MODE);
                break;

            case R.id.addParticipantButton:
                DialogFragment inputFragment = new InputDialog();
                inputFragment.show(getFragmentManager(), "AddParticipant");
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

    private void updateGroupDetails(String mode) {
        GroupDao.updateGroup(event.getId(), session.getLoggedInMail(), mode, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                boolean result = (boolean)objectMap.get("update_group_callback");
                if(result){
                    Utilities.shortMsg(context, "Event subscription updated.");
                    setResult(1111);
                    finish();
                }else{
                    Utilities.shortMsg(context, "error occurred, please try later!");
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(String emailAddress) {
        Log.d(Constatnts.TAG, "Clicked on add" + emailAddress);
        if(emailAddress.equals("") || !Utilities.isValidEmailAddress(emailAddress)){
            Utilities.shortMsg(context, emailAddress +" is not a valid email.");
        }else{
            if(checkIfUserAdded(emailAddress)){
                Utilities.longMsg(context, "User already added to the event.");
            }else{
                GroupDao.addUserToEvent(event, emailAddress, context);
            }
        }
    }

    public void onLocationChanged(Location location) {
        System.out.println("<==============onLocationChanged===========> ");
        System.out.println("loc change latt===========> " + location.getLatitude());
        System.out.println("loc change long===========> " + location.getLongitude());
        updateUserLocation(location.getLatitude(), location.getLongitude());
    }

    private void updateUserLocation(double latitude, double longitude) {
        User loggedinUser = session.getLoggedInUserDetails();
        loggedinUser.setLatitude(Double.toString(latitude));
        loggedinUser.setLongitude(Double.toString(longitude));
        loggedinUser.setLocationUpdateTime(getPacificCurrentDateTime());
        UserDao.checkAndUpdateUser(loggedinUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
            stopLocationTracking();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
            startLocationTracking();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(Constatnts.TAG, "Location services error code -> " + connectionResult.getErrorCode());
            }
        }
    }

    private void startLocationTracking(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)        // 20 second
                .setFastestInterval(1 * 1000); // 1 second
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            System.out.println("<============= update location is  null ==============>");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            System.out.println("<============= update location is not null ==============>");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            //handleNewLocation(location);
        }
    }

    private void stopLocationTracking(){
        if (Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode()) && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("event", event);
    }

    private Date getPacificCurrentDateTime(){
        //PMT date
        Date date = null;
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            String pacificDate = df.format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            date = sdf.parse(pacificDate);
        }catch(ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    private boolean checkIfUserAdded(String emailAddress) {
        boolean userAdded = false;
        for(User user : userDetails){
            if(user.getUserId().equalsIgnoreCase(emailAddress)) {
                userAdded = true;
                break;
            }
        }
        return userAdded;

    }

}


