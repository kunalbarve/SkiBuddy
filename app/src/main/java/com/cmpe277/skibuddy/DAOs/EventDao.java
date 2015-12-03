package com.cmpe277.skibuddy.DAOs;

import android.util.Log;
import android.widget.ListView;

import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.R;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
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
}
