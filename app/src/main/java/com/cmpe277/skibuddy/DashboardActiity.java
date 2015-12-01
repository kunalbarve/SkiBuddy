package com.cmpe277.skibuddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DashboardActiity extends AppCompatActivity {

   public ArrayList<Event> events = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQuery<Event> query = new ParseQuery<Event>("Event");

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("events", "Retrieved " + objects.size() + " events");


                    for (Event event : objects) {
                        Event newEvent = new Event();
                        newEvent.setEventName(event.getEventName());
                        newEvent.setDescription(event.getDescription());
                        events.add(newEvent);
                    }

                   // ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(DashboardActiity.this, R.layout.simplelistitem, events);


                    EventsAdapter adapter1 = new EventsAdapter(getApplicationContext(), events);





                    ListView listview = (ListView)findViewById(R.id.listView1);
                    listview.setAdapter(adapter1);

                    ListView listview2 = (ListView)findViewById(R.id.listView2);
                    listview2.setAdapter(adapter1);

                    ListView listview3 = (ListView)findViewById(R.id.listView3);
                    listview3.setAdapter(adapter1);

                    ListUtils.setDynamicHeight(listview);
                    ListUtils.setDynamicHeight(listview2);
                    ListUtils.setDynamicHeight(listview3);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }


            }
        });

            setContentView(R.layout.activity_dashboard_activity);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard_actiity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class EventsAdapter extends ArrayAdapter<Event> {

        private class ViewHolder
        {
            public  TextView eventName;
            public  TextView eventDescription;
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
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.eventName.setText(event.getEventName());
            viewHolder.eventDescription.setText(event.getDescription());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

class ListUtils {
    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}

