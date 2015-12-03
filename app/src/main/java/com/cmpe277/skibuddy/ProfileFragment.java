package com.cmpe277.skibuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{


    Button skietrackerButton = null;

    Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity().getApplicationContext();
        skietrackerButton = (Button)v.findViewById(R.id.skiTrackerButton);
        skietrackerButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.skiTrackerButton :
                Intent skiTrackerIntent = new Intent(context, SkiTrackerActivity.class);
                startActivity(skiTrackerIntent);
                break;
        }
    }
}
