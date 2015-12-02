package com.cmpe277.skibuddy;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkiTrackerActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    public static final String CLASSNAME = SkiTrackerActivity.class.getSimpleName();

    private final static int GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER = 7878;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private double lastLattitude;
    private double lastLongitude;
    private float traveledDistance; //in meters
    private List<Double> lattitudeList;
    private List<Double> longitudeList;

    private Button startButton;
    private Button stopButton;

    //timer
    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    private TextView timerTextView;

    //distance
    private TextView distanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ski_tracker);

        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        timerTextView = (TextView)findViewById(R.id.textTimer);
        distanceTextView = (TextView)findViewById(R.id.textDistance);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        googleApiClient.connect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    private void handleNewLocation(Location location) {
        Log.d(CLASSNAME, location.toString());
        if(lastLattitude==0.0 && lastLongitude==0.0){
            lastLattitude=location.getLatitude();
            lastLongitude=location.getLongitude();
            lattitudeList.add(lastLattitude);
            longitudeList.add(lastLongitude);
            return;
        }
        System.out.println("latt===========> " + location.getLatitude());
        System.out.println("long===========> "+location.getLongitude());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        //MarkerOptions options = new MarkerOptions()
        //        .position(latLng)
        //       .title("");
        //mMap.addMarker(options);
        PolylineOptions lineOptions = new PolylineOptions()
                .add(new LatLng(lastLattitude, lastLongitude))
                .add(new LatLng(currentLatitude, currentLongitude));
        Polyline polyline = mMap.addPolyline(lineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        //update distance
        addToTraveledDistance(lastLattitude, lastLongitude, currentLatitude, currentLongitude);
        lastLattitude=currentLatitude;
        lastLongitude=currentLongitude;
        lattitudeList.add(lastLattitude);
        longitudeList.add(lastLongitude);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("<==============onLocationChanged===========> ");
        System.out.println("loc change latt===========> " + location.getLatitude());
        System.out.println("loc change long===========> "+location.getLongitude());
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(CLASSNAME, "Location services error code -> " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationTracking();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.startButton :
                startLocationTracking();
                break;
            case R.id.stopButton :
                stopLocationTracking();
                break;
        }
    }

    private void startLocationTracking(){
        setUpMapIfNeeded();
        lattitudeList = new ArrayList<Double>();
        longitudeList = new ArrayList<Double>();
        lastLongitude=0.0;
        lastLattitude=0.0;
        traveledDistance=0.0f;
        startButton.setEnabled(false);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 1 second
                .setFastestInterval(1 * 1000); // 1 second
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            System.out.println("<============= location is not null ==============>");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            System.out.println("<============= location is null ==============>");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            //handleNewLocation(location);
        }
        //start timer
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(updateTimerMethod, 0);
    }


    private void stopLocationTracking(){
        //stop timer
        timeSwap += timeInMillies;
        timerHandler.removeCallbacks(updateTimerMethod);

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        stopButton.setEnabled(false);
        System.out.println("<====total traveled distance: " + traveledDistance + " meters");
        System.out.println("<====print points ======>");
    }

    private void addToTraveledDistance(double lastLattitude, double lastLongitudem, double currentLattitude, double currentLongitude){
        Location location1 = new Location("");
        location1.setLatitude(lastLattitude);
        location1.setLongitude(lastLongitudem);
        Location location2 = new Location("");
        location2.setLatitude(currentLattitude);
        location2.setLongitude(currentLongitude);
        traveledDistance = traveledDistance + location1.distanceTo(location2);
        distanceTextView.setText(traveledDistance +"meters");
    }


    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            timerTextView.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
            + String.format("%03d", milliseconds));
            timerHandler.postDelayed(this, 0);
        }

    };
}
