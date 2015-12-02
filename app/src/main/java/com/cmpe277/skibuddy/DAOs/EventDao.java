package com.cmpe277.skibuddy.DAOs;

import android.util.Log;
import android.widget.ListView;

import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class EventDao {

    public static void getActiveEventDetails(final EventsAdapter adapter, final List<Event> activeEvents, final ListView listView){
        ParseQuery<ParseObject> query = new ParseQuery<>("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
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
        ParseQuery<ParseObject> query = new ParseQuery<>("Event");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
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
