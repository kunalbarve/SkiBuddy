package com.cmpe277.skibuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener{


    private Button skiTrackerButton = null;
    private ImageView profilePic;
    private TextView userName;
    private TextView userEmail;
    private TextView tagLine;
    private SessionManager session;
    private View v;
    Context context;

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

            skiTrackerButton = (Button)v.findViewById(R.id.skiTrackerButton);
            skiTrackerButton.setOnClickListener(this);
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
