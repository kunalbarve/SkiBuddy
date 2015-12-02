package com.cmpe277.skibuddy;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Utility.ListUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {


    public DashboardFragment() {

    }

    public ArrayList<Event> events = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("events", "Retrieved " + objects.size() + " events");


                    for (ParseObject event : objects) {
                        Event newEvent = new Event();
                        newEvent.setName(event.getString("eventName"));
                        newEvent.setDescription(event.getString("description"));
                        events.add(newEvent);
                    }

                    // ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(DashboardActiity.this, R.layout.simplelistitem, events);


                    EventsAdapter adapter1 = new EventsAdapter(getActivity().getApplicationContext(), events);


                    ListView listview = (ListView) v.findViewById(R.id.listView1);
                    listview.setAdapter(adapter1);

                    ListView listview2 = (ListView) v.findViewById(R.id.listView2);
                    listview2.setAdapter(adapter1);

                    ListView listview3 = (ListView) v.findViewById(R.id.listView3);
                    listview3.setAdapter(adapter1);

                    ListUtils.setDynamicHeight(listview);
                    ListUtils.setDynamicHeight(listview2);
                    ListUtils.setDynamicHeight(listview3);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });

        return v;

    }


    public class EventsAdapter extends ArrayAdapter<Event> {

        private class ViewHolder {
            public TextView eventName;
            public TextView eventDescription;
        }


        public EventsAdapter(Context context, ArrayList<Event> events) {
            super(context, 0, events);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Event event = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.simplelistitem, parent, false);
                viewHolder.eventName = (TextView) convertView.findViewById(R.id.eventName);
                viewHolder.eventDescription = (TextView) convertView.findViewById(R.id.eventDescription);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.eventName.setText(event.getName());
            viewHolder.eventDescription.setText(event.getDescription());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}



