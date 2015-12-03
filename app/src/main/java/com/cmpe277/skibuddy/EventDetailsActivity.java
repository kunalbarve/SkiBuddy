package com.cmpe277.skibuddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Utility.SessionManager;

public class EventDetailsActivity extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        session = new SessionManager(context);

        if(session.checkLogin()){
            setContentView(R.layout.activity_event_details);

            Bundle bundle = this.getIntent().getExtras();
            if(bundle==null){
                return;
            }
            event = (Event)bundle.getSerializable("event");

        }else{
            finish();
        }


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
        }

        return super.onOptionsItemSelected(item);
    }
}
