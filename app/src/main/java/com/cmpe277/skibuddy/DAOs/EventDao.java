package com.cmpe277.skibuddy.DAOs;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.R;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class EventDao {

    public static void getActiveEventDetails(List<String> eventIds, final ParseReceiveAsyncObjectListener listenerObj){

        Date currentDate = Utilities.getPacificCurrentDateTime();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        query.whereContainedIn("objectId", eventIds);
        query.whereLessThanOrEqualTo("startDate", currentDate);
        query.whereGreaterThanOrEqualTo("endDate", currentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG,"Came in fetching event details"+objects.size());
                    ArrayList<Event> events = new ArrayList<Event>();
                    for (ParseObject obj : objects) {
                        Event event = new Event();
                        event.setName(obj.getString("eventName"));
                        event.setDescription(obj.getString("description"));
                        event.setId(obj.getObjectId());
                        event.setStartDate(obj.getDate("startDate"));
                        event.setEndDate(obj.getDate("endDate"));
                        event.setStartTime(obj.getString("startTime"));
                        event.setEndTime(obj.getString("endTime"));

                        Log.d("EVENT", event.toString());
                        String current = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date());
                        Log.d("EVENT", current);

                        events.add(event);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setEventDetails(events);
                    Log.d(Constatnts.TAG, "Callback set in event");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("activated_callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }


    public static void getParticipatingEvents(List<String> eventIds, final ParseReceiveAsyncObjectListener listenerObj){

        Date currentDate = Utilities.getPacificCurrentDateTime();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        query.whereContainedIn("objectId", eventIds);
        query.whereGreaterThan("startDate", currentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG,"Came in fetching event details"+objects.size());
                    ArrayList<Event> events = new ArrayList<Event>();
                    for (ParseObject obj : objects) {
                        Event event = new Event();
                        event.setName(obj.getString("eventName"));
                        event.setDescription(obj.getString("description"));
                        event.setId(obj.getObjectId());
                        event.setStartDate(obj.getDate("startDate"));
                        event.setEndDate(obj.getDate("endDate"));
                        event.setStartTime(obj.getString("startTime"));
                        event.setEndTime(obj.getString("endTime"));

                        events.add(event);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setEventDetails(events);
                    Log.d(Constatnts.TAG, "Callback set in event");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("joined_callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });

    }

    public static void getInvitedEvents(List<String> eventIds, final ParseReceiveAsyncObjectListener listenerObj){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        query.whereContainedIn("objectId", eventIds);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG,"Came in fetching event details"+objects.size());
                    ArrayList<Event> events = new ArrayList<Event>();
                    for (ParseObject obj : objects) {
                        Event event = new Event();
                        event.setName(obj.getString("eventName"));
                        event.setDescription(obj.getString("description"));
                        event.setId(obj.getObjectId());
                        event.setStartDate(obj.getDate("startDate"));
                        event.setEndDate(obj.getDate("endDate"));
                        event.setStartTime(obj.getString("startTime"));
                        event.setEndTime(obj.getString("endTime"));

                        events.add(event);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setEventDetails(events);
                    Log.d(Constatnts.TAG, "Callback set in event");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("invited_callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });

    }

    public static void getEventDetailAndStartIntent(String eventId, final Record recordData, final ParseReceiveAsyncObjectListener listnerForObjects){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        final Event fetchedEvent = new Event();
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            public void done(ParseObject record, ParseException e) {
                if (e == null) {
                    fetchedEvent.setId(record.getString("id"));
                    fetchedEvent.setName(record.getString("eventName"));
                    fetchedEvent.setDescription(record.getString("description"));
                    fetchedEvent.setStartDate(record.getDate("startDate"));
                    fetchedEvent.setStartTime(record.getString("startTime"));
                    fetchedEvent.setEndDate(record.getDate("endDate"));
                    fetchedEvent.setEndTime(record.getString("endTime"));

                    HashMap<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("record", recordData);
                    objectMap.put("event", fetchedEvent);
                    listnerForObjects.receiveObjects(objectMap);
                } else {
                    Log.d("records", "Error: " + e.getMessage());
                }
            }
        });
    }


    public static void getParticipatedEventDetailsForUser(List<String> eventIds, final ParseReceiveAsyncObjectListener listenerObj){

        Date currentDate = new Date();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        query.whereContainedIn("objectId", eventIds);
        query.whereLessThanOrEqualTo("endDate", currentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG, "Came in fetching event details" + objects.size());
                    ArrayList<Event> events = new ArrayList<Event>();
                    for (ParseObject obj : objects) {
                        Event event = new Event();
                        event.setName(obj.getString("eventName"));
                        event.setDescription(obj.getString("description"));
                        event.setId(obj.getObjectId());
                        event.setStartDate(obj.getDate("startDate"));
                        event.setEndDate(obj.getDate("endDate"));
                        event.setStartTime(obj.getString("startTime"));
                        event.setEndTime(obj.getString("endTime"));

                        events.add(event);
                    }

                    HashMap<String, Object> objectList = new HashMap<String, Object>();
                    objectList.put("pastEventsForUserId",events);
                    listenerObj.receiveObjects(objectList);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

}
