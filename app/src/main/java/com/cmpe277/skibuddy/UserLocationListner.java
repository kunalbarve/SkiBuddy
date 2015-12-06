package com.cmpe277.skibuddy;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.cmpe277.skibuddy.DAOs.UserDao;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Swapnil on 12/5/2015.
 */
public class UserLocationListner implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //get user location if event is active one
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private final static int GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER = 8787;
    private Context context;
    private User user;

    UserLocationListner(Context context, User user){
        this.context = context;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.user=user;
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationTracking();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((Activity)context, GOOGLESERVICE_CONNECTION_FAILURE_RESOLUTION_REQUEST_NUMBER);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(Constatnts.TAG, "Location services error code -> " + connectionResult.getErrorCode());
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

    public void stopLocationTracking(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
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

    private void updateUserLocation(User user, double latitude, double longitude) {
        user.setLatitude(Double.toString(latitude));
        user.setLongitude(Double.toString(longitude));
        user.setLocationUpdateTime(getPacificCurrentDateTime());
        UserDao.checkAndUpdateUser(user);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("<==============onLocationChanged===========> ");
        System.out.println("loc change latt===========> " + location.getLatitude());
        System.out.println("loc change long===========> " + location.getLongitude());
        updateUserLocation(user , location.getLatitude(), location.getLongitude());
    }
}
