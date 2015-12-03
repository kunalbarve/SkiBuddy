package com.cmpe277.skibuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;

import com.cmpe277.skibuddy.DAOs.EventDao;
import com.cmpe277.skibuddy.DAOs.GroupDao;
import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.ListUtility.ListUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    public ArrayList<Event> activeEvents = new ArrayList<>();
    public ArrayList<Event> participatingEvents = new ArrayList<>();
    public ArrayList<Event> invitedEvents = new ArrayList<>();

    private SessionManager session;

    Context context;

    private ListView listView;
    private ListView listView2;
    private ListView listView3;
    private Button createEventButton;

    public DashboardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getActivity().getApplicationContext();

        session = new SessionManager(context);

        listView = (ListView) v.findViewById(R.id.listView1);
        listView2 = (ListView) v.findViewById(R.id.listView2);
        listView3 = (ListView) v.findViewById(R.id.listView3);

        getGroupDetails();

        createEventButton = (Button) v.findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(this);

        return v;

    }

    private void getGroupDetails() {
        GroupDao.getGroupDetails(session.getLoggedInMail(), new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("callback");
                    List<Group> groupDetails = callback.getGroupDetails();
                    Log.d(Constatnts.TAG, "" + groupDetails.size());

                    List<List<String>> ids = separateEventIds(groupDetails);

                    List<String> acceptedEventIds = ids.get(0);
                    List<String> invitationIds = ids.get(1);

                    refreshActivatedEvents(acceptedEventIds);
                    refreshJoinedEvents(acceptedEventIds);
                    refreshInvitedEvents(invitationIds);


                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }

    private void refreshActivatedEvents(List<String> eventIds){
        EventDao.getActiveEventDetails(eventIds, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("activated_callback");
                    participatingEvents = callback.getEventDetails();
                    Log.d(Constatnts.TAG, "" + participatingEvents.size());
                    EventsAdapter adapter1 = new EventsAdapter(context, activeEvents);
                    listView.setAdapter(adapter1);
                    ListUtils.setDynamicHeight(listView);

                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }

    private void refreshJoinedEvents(List<String> eventIds){
        EventDao.getParticipatingEvents(eventIds, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("joined_callback");
                    participatingEvents = callback.getEventDetails();
                    Log.d(Constatnts.TAG, "" + participatingEvents.size());
                    EventsAdapter adapter2 = new EventsAdapter(context, participatingEvents);
                    listView2.setAdapter(adapter2);
                    ListUtils.setDynamicHeight(listView2);

                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }

    private void refreshInvitedEvents(List<String> eventIds){
        EventDao.getInvitedEvents(eventIds, new ParseReceiveAsyncObjectListener() {
            @Override
            public void receiveObjects(HashMap<String, Object> objectMap) {
                Log.d(Constatnts.TAG, "Got Response set");
                try {
                    CallbackUtils callback = (CallbackUtils) objectMap.get("invited_callback");
                    participatingEvents = callback.getEventDetails();
                    Log.d(Constatnts.TAG, "" + participatingEvents.size());
                    EventsAdapter adapter3 = new EventsAdapter(context, invitedEvents);
                    listView3.setAdapter(adapter3);
                    ListUtils.setDynamicHeight(listView3);

                } catch (Exception e) {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }

    private List<List<String>> separateEventIds(List<Group> groupDetails) {
        List<List<String>> result = new ArrayList<>();
        List<String> invitationIds = new ArrayList<>();
        List<String> acceptedEventIds = new ArrayList<>();

        for (Group g : groupDetails) {
            if (g.getStatus().equals("1"))
                acceptedEventIds.add(g.getEventId());
            else
                invitationIds.add(g.getEventId());
        }

        result.add(acceptedEventIds);
        result.add(invitationIds);

        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createEventButton:
                Intent createEventIntent = new Intent(context, CreateEvent.class);
                startActivity(createEventIntent);
                break;
        }
    }
}



