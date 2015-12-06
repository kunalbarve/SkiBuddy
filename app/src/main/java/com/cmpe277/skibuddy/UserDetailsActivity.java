package com.cmpe277.skibuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.RecordDao;
import com.cmpe277.skibuddy.DAOs.UserDao;
import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
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
    private Button displayLocationButton;
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

        Bundle bundle = this.getIntent().getExtras();
        if(bundle==null){
            return;
        }
        event = (Event)bundle.getSerializable("event");
        user = (User)bundle.getSerializable("user");


        profilePic = (ImageView) findViewById(R.id.profilePicUserDetails);
        userName = (TextView) findViewById(R.id.userNameUserDetails);
        userEmail = (TextView) findViewById(R.id.emailUserDetails);
        tagLine = (TextView) findViewById(R.id.tagLineUserDetails);
        if(Constatnts.ACTIVATED_MODE.equalsIgnoreCase(event.getMode())){
            displayLocationButton = (Button)findViewById(R.id.displayUserLocation);
            displayLocationButton.setVisibility(View.VISIBLE);
            final ParseReceiveAsyncObjectListener listener = this;
            displayLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> userIds = new ArrayList<String>();
                    userIds.add(user.getUserId());
                    UserDao.getUserDetails(userIds,listener);
                }
            });
        }

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
        setTitle("Explore Users");
    }

    public void displayRecordList(List<Record> recordList){
        listView.setAdapter(new RecordListAdapter(getApplicationContext(), R.layout.record_item, recordList));
    }

    @Override
    public void receiveObjects(HashMap<String, Object> objectMap) {
        if(objectMap==null){
            return;
        }
        if(objectMap.containsKey("records")){
            recordList=(ArrayList<Record>)objectMap.get("records");
            displayRecordList(recordList);
        }else {
            if(objectMap.containsKey("user_callback")){
                ArrayList<User> userDetails = ((CallbackUtils)objectMap.get("user_callback")).getUserDetails();
                System.out.println("<===here "+userDetails.size());
                if(userDetails.size()!=1){
                    return;
                }
                User user1 = userDetails.get(0);
                Intent displayUserLocationIntent = new Intent(context, UserLocationActivity.class);
                displayUserLocationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putSerializable("user", user1);
                extras.putSerializable("event", event);
                displayUserLocationIntent.putExtras(extras);
                context.startActivity(displayUserLocationIntent);
            }
        }
    }

    private void updateUserDetails(){
        if(!user.getImage().equals(""))
            Picasso.with(context).load(user.getImage()).into(profilePic);

        userName.setText(user.getUserName());
        userEmail.setText(user.getUserId());
        tagLine.setText(user.getTagLine());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            session.logoutUser();
            finish();
            return true;
        }else if(id == R.id.updateProfile){
            String url = session.getLoggedInUserDetails().getUrl();
            if(url.equals(""))
                Utilities.shortMsg(getApplicationContext(), "User profile information missing. Try later!");
            else{
                session.logoutUser();
                finish();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
