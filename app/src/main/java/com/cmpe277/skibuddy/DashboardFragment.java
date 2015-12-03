package com.cmpe277.skibuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;

import com.cmpe277.skibuddy.DAOs.EventDao;
import com.cmpe277.skibuddy.ListUtility.EventsAdapter;
import com.cmpe277.skibuddy.Models.Event;
import java.util.ArrayList;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    public ArrayList<Event> activeEvents = new ArrayList<>();
    public ArrayList<Event> participatingEvents = new ArrayList<>();
    public ArrayList<Event> invitedEvents = new ArrayList<>();

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

        listView = (ListView) v.findViewById(R.id.listView1);
        listView2 = (ListView) v.findViewById(R.id.listView2);
        listView3 = (ListView) v.findViewById(R.id.listView3);

        EventsAdapter adapter1 = new EventsAdapter(context, activeEvents);
        EventsAdapter adapter2 = new EventsAdapter(context, participatingEvents);
        EventsAdapter adapter3 = new EventsAdapter(context, invitedEvents);

        listView.setAdapter(adapter1);
        listView2.setAdapter(adapter1);
        listView3.setAdapter(adapter1);

        EventDao.getActiveEventDetails(adapter1, activeEvents, listView);
        EventDao.getParticipatingEvents(adapter2, participatingEvents, listView2);
        EventDao.getInvitedEvents(adapter3, invitedEvents, listView3);

        createEventButton = (Button) v.findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.createEventButton :
                Intent createEventIntent = new Intent(context, CreateEvent.class);
                startActivity(createEventIntent);
                break;
        }
    }
}



