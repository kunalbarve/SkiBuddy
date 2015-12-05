package com.cmpe277.skibuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.EventDao;
import com.cmpe277.skibuddy.DAOs.GroupDao;
import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.squareup.picasso.Picasso;

import com.cmpe277.skibuddy.DAOs.RecordDao;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment implements ParseReceiveAsyncObjectListener{

    private ImageView profilePic;
    private TextView userName;
    private TextView userEmail;
    private TextView tagLine;
    private SessionManager session;
    private View v;
    ListView listView;

    Context context;

    ArrayList<Event> events;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        session = new SessionManager(context);

        if(session.checkLogin()){
            v = inflater.inflate(R.layout.fragment_profile, container, false);

            profilePic = (ImageView) v.findViewById(R.id.profilePic);
            userName = (TextView) v.findViewById(R.id.userName);
            userEmail = (TextView) v.findViewById(R.id.email);
            tagLine = (TextView) v.findViewById(R.id.tagLine);

            updateUserDetails();

            listView = (ListView)v.findViewById(R.id.eventView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
                    Event event = events.get(pos);
                    getEventDetails(event, Constatnts.PARTICIPATE_MODE);
                }
            });
            //load  participated events for user
            GroupDao.getGroupDetails(session.getLoggedInMail(), this);
        }else{
            getActivity().finish();
        }

        return v;
    }

    private void updateUserDetails(){
        User user = session.getLoggedInUserDetails();

            if(!user.getImage().equals(""))
                Picasso.with(context).load(user.getImage()).into(profilePic);

            userName.setText(user.getUserName());
            userEmail.setText(user.getUserId());
            tagLine.setText(user.getTagLine());
    }


    public void displayEventList(ArrayList<Event> eventList){
        listView.setAdapter(new EventsAdapter(context, eventList));
    }

    @Override
    public void receiveObjects(HashMap<String, Object> objectMap) {
        if(objectMap.containsKey("callback")){
            List<Group> groupDetails = ((CallbackUtils)objectMap.get("callback")).getGroupDetails();
            EventDao.getParticipatedEventDetailsForUser(getEventIdsFromGroups(groupDetails), this);
        }
        else if(objectMap.containsKey("pastEventsForUserId")){
            events = (ArrayList<Event>)objectMap.get("pastEventsForUserId");
            displayEventList(events);
        }
    }

    private List<String> getEventIdsFromGroups(List<Group> gorupList){
        ArrayList<String> eventIdList = new ArrayList<String>();
        for(Group group: gorupList){
            if("1".equals(group.getStatus())){
                eventIdList.add(group.getEventId());
            }
        }
        return eventIdList;
    }


    private void getEventDetails(Event event, String mode){
        if(event != null){
            event.setMode(mode);

            Bundle extras = new Bundle();
            extras.putSerializable("event", event);

            Intent eventDetailIntent = new Intent(context, EventDetailsActivity.class);
            eventDetailIntent.putExtras(extras);
            startActivity(eventDetailIntent);
            Log.d(Constatnts.TAG, "EventId:" + event.toString());
        }else
            Utilities.shortMsg(context, "Event details not available.");
    }
}
