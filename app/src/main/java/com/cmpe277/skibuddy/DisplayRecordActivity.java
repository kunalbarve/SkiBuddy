package com.cmpe277.skibuddy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;

/**
 * Created by Swapnil on 12/2/2015.
 */
public class DisplayRecordActivity extends FragmentActivity {

    public static final String CLASSNAME = DisplayRecordActivity.class.getSimpleName();

    private Event event;
    private Record record;

    private TextView textRecordName;
    private TextView textEventName;
    private TextView textDistanceCovered;
    private TextView textTraveledTime;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_record);

        //get object map
        Bundle bundle = this.getIntent().getExtras();
        if(bundle==null){
            return;
        }
        event = (Event)bundle.getSerializable("event");
        record = (Record)bundle.getSerializable("record");

        //initialize view objects
        textRecordName = (TextView)findViewById(R.id.textRecordName);
        textEventName = (TextView)findViewById(R.id.textEventName);
        textDistanceCovered = (TextView)findViewById(R.id.textDistanceCovered);
        textTraveledTime = (TextView)findViewById(R.id.textTraveledTime);

        //display values for textview
        displayTextViewValues();

        //display path on map
        setUpMapIfNeeded();
    }

    private void displayTextViewValues() {

        textRecordName.setText(record.getStartTime()+"-"+record.getEndTime());
        textDistanceCovered.setText(Double.toString(record.getDistance()));
        textEventName.setText(event.getName().trim());
        textTraveledTime.setText(record.getTotalTime().trim());

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pathmap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                plotPathOnMap();
            }
        }
    }

    private void plotPathOnMap() {

        String pathString = record.getPath().trim();
        if(pathString==null || pathString.isEmpty()){
            return;
        }
        String[] latlongStrings = pathString.split(",");
        if(latlongStrings==null || latlongStrings.length<1){
            return;
        }

        double lastLattitude = 0.0;
        double lastLongitude = 0.0;
        for(int point=0; point<latlongStrings.length; point++){
            String lat_log_string = latlongStrings[point].trim();
            if(lat_log_string == null || lat_log_string.isEmpty()){
                continue;
            }
            String[] lat_long = lat_log_string.split(":");
            if(lat_long==null || lat_long.length!=2){
                continue;
            }
            double lattitude = Double.parseDouble(lat_long[0]);
            double longitude = Double.parseDouble(lat_long[1]);
            if(point==0){
                //start point
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(lattitude, longitude)).title("start point");
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lattitude, longitude)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 14));
                lastLattitude = lattitude;
                lastLongitude = longitude;
                continue;
            }
            if(point==latlongStrings.length-1){
                //end point
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(lattitude, longitude)).title("end point");
                mMap.addMarker(options);
            }
            //use polyline to draw line between last and current point
            PolylineOptions lineOptions = new PolylineOptions()
                    .add(new LatLng(lastLattitude, lastLongitude))
                    .add(new LatLng(lattitude, longitude));
            Polyline polyline = mMap.addPolyline(lineOptions);
            lastLattitude = lattitude;
            lastLongitude = longitude;
        }
    }

}
