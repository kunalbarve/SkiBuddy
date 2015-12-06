package com.cmpe277.skibuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.RecordDao;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Swapnil on 12/4/2015.
 */
public class UserDetailsActivity extends AppCompatActivity implements ParseReceiveAsyncObjectListener{

    private ImageView profilePic;
    private TextView userName;
    private TextView userEmail;
    private TextView tagLine;
    private SessionManager session;

    private Context context;
    ListView listView;

    private Event event;
    private User user;

    private ArrayList<Record> recordList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        session = new SessionManager(context);

        setContentView(R.layout.activity_user_details);

        profilePic = (ImageView) findViewById(R.id.profilePicUserDetails);
        userName = (TextView) findViewById(R.id.userNameUserDetails);
        userEmail = (TextView) findViewById(R.id.emailUserDetails);
        tagLine = (TextView) findViewById(R.id.tagLineUserDetails);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle==null){
            return;
        }
        event = (Event)bundle.getSerializable("event");
        user = (User)bundle.getSerializable("user");

        listView = (ListView)findViewById(R.id.recordsViewUserDetails);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
                Record record = recordList.get(pos);
                Intent displayRecordIntent = new Intent(context, DisplayRecordActivity.class);
                displayRecordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putSerializable("event", event);
                extras.putSerializable("record", record);
                displayRecordIntent.putExtras(extras);
                context.startActivity(displayRecordIntent);
            }
        });
        updateUserDetails();
        //load  records for user
        RecordDao.getRecordsForUserId(user.getUserId(), event.getId(), this);
    }

    public void displayRecordList(List<Record> recordList){
        listView.setAdapter(new RecordListAdapter(getApplicationContext(), R.layout.record_item, recordList));
    }

    @Override
    public void receiveObjects(HashMap<String, Object> objectMap) {
        if(objectMap.containsKey("records")){
            recordList=(ArrayList<Record>)objectMap.get("records");
            displayRecordList(recordList);
        }
    }

    private void updateUserDetails(){
        if(!user.getImage().equals(""))
            Picasso.with(context).load(user.getImage()).into(profilePic);

        userName.setText(user.getUserName());
        userEmail.setText(user.getUserId());
        tagLine.setText(user.getTagLine());
    }

}