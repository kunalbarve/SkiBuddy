package com.cmpe277.skibuddy.ListUtility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.R;

import java.util.ArrayList;

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
        Event event = getItem(position);
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
        viewHolder.eventName.setText(event.getName());
        viewHolder.eventDescription.setText(event.getDescription());

        return convertView;
    }
}