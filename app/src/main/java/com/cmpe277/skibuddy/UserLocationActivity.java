package com.cmpe277.skibuddy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.UserDao;
import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Swapnil on 12/5/2015.
 */
public class UserLocationActivity extends FragmentActivity implements View.OnClickListener, ParseReceiveAsyncObjectListener{

    private SessionManager session;
    private Context context;

    private User user;
    private User loggedinUser;
    private Event event;

    private Button userLocationButton;
    private Button yourLocationButton;
    private TextView locationMessageTextView;
    private GoogleMap mMap;

    //marker position
    Marker userMarker;
    Marker loggedinUserMarker;

    Timer timer;
    FetchLocationTask fetchLocationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        //get object map
        Bundle bundle = this.getIntent().getExtras();
        if(bundle==null){
            return;
        }
        user = (User)bundle.getSerializable("user");
        event = (Event)bundle.getSerializable("event");

        context = getApplicationContext();
        session = new SessionManager(context);
        loggedinUser = (User)session.getLoggedInUserDetails();

        //initialize view objects
        userLocationButton = (Button)findViewById(R.id.userLocationButton);
        yourLocationButton = (Button)findViewById(R.id.yourLocationButton);
        userLocationButton.setOnClickListener(this);
        yourLocationButton.setOnClickListener(this);
        locationMessageTextView = (TextView)findViewById(R.id.textLocationMessage);

        if(loggedinUser.getUserId().equalsIgnoreCase(user.getUserId())){
            userLocationButton.setVisibility(View.INVISIBLE);
        }


        //display path on map
        setUpMapIfNeeded();

        //set uo timer
        timer = new Timer();
        fetchLocationTask = new FetchLocationTask(user, loggedinUser, this);
        timer.schedule(fetchLocationTask, 1000, 5000);//in ms delay=1sec, repeat=5sec
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.userLocationMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                plotUserLocationOnMap(user, userMarker, true, decisionToDisplayLocation());
            }
        }
    }

    private void plotUserLocationOnMap(User user, Marker marker, boolean zoom, boolean decision) {

        if(user.getLatitude()!= null && !user.getLatitude().trim().isEmpty() &&
                user.getLongitude()!=null && !user.getLongitude().trim().isEmpty() && decision){
            //User Location
            double latitude = Double.parseDouble(user.getLatitude());
            double longitude = Double.parseDouble(user.getLongitude());
            if(marker==null){
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(latitude, longitude)).title(user.getUserName());
                if(user.getUserId().equalsIgnoreCase(session.getLoggedInUserDetails().getUserId())){
                    loggedinUserMarker = mMap.addMarker(options);
                }else{
                    userMarker = mMap.addMarker(options);
                }
            }else{
                marker.setPosition(new LatLng(latitude, longitude));
            }
            if(zoom)
                moveCameraAndZoom(latitude, longitude, 13);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            locationMessageTextView.setText("User location last updated on " + simpleDateFormat.format(user.getLocationUpdateTime()));
        }else{
            locationMessageTextView.setText("User never logged in after this event start");
            userLocationButton.setVisibility(View.INVISIBLE);
        }

    }

    private boolean decisionToDisplayLocation() {
        //if location update after event start then only display location
        boolean decision = false;

        if(user.getLocationUpdateTime().after(event.getStartDate())){
            decision=true;
        }

        return decision;
    }

    private void moveCameraAndZoom(double lattitude, double longitude, int zoomLevel){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lattitude, longitude)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), zoomLevel));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLocationButton:
                moveCameraAndZoom(Double.parseDouble(user.getLatitude().trim()), Double.parseDouble(user.getLongitude().trim()), 13);
                break;
            case R.id.yourLocationButton:
                moveCameraAndZoom(Double.parseDouble(loggedinUser.getLatitude().trim()), Double.parseDouble(loggedinUser.getLongitude().trim()), 13);
                break;
        }

    }

    @Override
    public void receiveObjects(HashMap<String, Object> objectMap) {
        if(objectMap==null || !objectMap.containsKey("user_callback")){
            return;
        }
        ArrayList<User> userDetails = ((CallbackUtils)objectMap.get("user_callback")).getUserDetails();
        User user1 = userDetails.get(0);
        User user2;
        if(userDetails.size()==2){
            user2 = userDetails.get(1);
        }else{
            user2 = user1;
        }

        if(user1.getUserId().equalsIgnoreCase(loggedinUser.getUserId())){
            loggedinUser=user1;
            user=user2;
        }else{
            loggedinUser=user2;
            user=user1;
        }
        plotUserLocationOnMap(loggedinUser, loggedinUserMarker, false, true);
        plotUserLocationOnMap(user, userMarker, false, decisionToDisplayLocation());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }
}


class FetchLocationTask extends TimerTask {

    User user;
    User loggedinUser;
    ParseReceiveAsyncObjectListener listenerObj;

    FetchLocationTask(User user, User loggedinUser, ParseReceiveAsyncObjectListener listenerObj){
        this.user=user;
        this.loggedinUser=loggedinUser;
        this.listenerObj=listenerObj;
    }

    @Override
    public void run() {
        //get user location
        List<String> userIds = new ArrayList<String>();
        userIds.add(user.getUserId());
        userIds.add(loggedinUser.getUserId());
        UserDao.getUserDetails(userIds,listenerObj);
    }

}