package com.cmpe277.skibuddy.ListUtility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.R;
import com.cmpe277.skibuddy.SkiTrackerActivity;
import com.cmpe277.skibuddy.UserDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private Event event;

    private class ViewHolder {
        public ImageView pic;
        public TextView name;
    }


    public UserAdapter(Context context, ArrayList<User> userDetails, Event event) {
        super(context, 0, userDetails);
        this.context = context;
        this.event = event;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participants_list_item, parent, false);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.participantPic);
            viewHolder.name = (TextView) convertView.findViewById(R.id.participantName);
            convertView.setTag(viewHolder.name);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!user.getImage().equals(""))
            Picasso.with(context).load(user.getImage()).into(viewHolder.pic);

        viewHolder.name.setText(user.getUserName());

        return convertView;
    }

}