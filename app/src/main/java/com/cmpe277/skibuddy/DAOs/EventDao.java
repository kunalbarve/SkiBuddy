package com.cmpe277.skibuddy.DAOs;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.R;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class EventDao {

    public static void getActiveEventDetails(final EventsAdapter adapter, final List<Event> activeEvents, final ListView listView, SessionManager session){


        String userId = session.getLoggedInMail();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Group");
        query.whereEqualTo("userId", userId);
        query.whereEqualTo("flag", "1");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    List<String> eventIds = new ArrayList<String>();
                     for (ParseObject group : objects) {
                       eventIds.add(group.getString("eventId"));
                    }

                    fetchActiveEvents(eventIds, adapter, activeEvents, listView);
                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void fetchActiveEvents(List<String> eventIds,final EventsAdapter adapter, final List<Event> activeEvents, final ListView listView){
        Date currentDate = new Date();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        query.whereContainedIn("eventId", eventIds);
        query.whereLessThanOrEqualTo("startDate", currentDate);
        query.whereGreaterThanOrEqualTo("endDate", currentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("1 size",Integer.toString(objects.size()));
                    for (ParseObject event : objects) {
                        Event newEvent = new Event();
                        newEvent.setName(event.getString("eventName"));
                        newEvent.setDescription(event.getString("description"));

                        activeEvents.add(newEvent);
                        adapter.notifyDataSetChanged();
                        ListUtils.setDynamicHeight(listView);
                    }

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getParticipatingEvents(final EventsAdapter adapter, final List<Event> participatingEvents, final ListView listView){

        final ParseQuery<ParseObject> query = new ParseQuery<>("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("1 size",Integer.toString(objects.size()));
                    for (ParseObject event : objects) {
                        Event newEvent = new Event();
                        newEvent.setName(event.getString("eventName"));
                        newEvent.setDescription(event.getString("description"));
                        participatingEvents.add(newEvent);
                        adapter.notifyDataSetChanged();
                        ListUtils.setDynamicHeight(listView);
                    }
                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });

    }

    public static void getInvitedEvents(final EventsAdapter adapter, final List<Event> invitedEvents, final ListView listView){

        ParseQuery<ParseObject> query = new ParseQuery<>("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("2 size",Integer.toString(objects.size()));
                    for (ParseObject event : objects) {
                        Event newEvent = new Event();
                        newEvent.setName(event.getString("eventName"));
                        newEvent.setDescription(event.getString("description"));
                        invitedEvents.add(newEvent);
                        adapter.notifyDataSetChanged();
                        ListUtils.setDynamicHeight(listView);
                    }
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

}
