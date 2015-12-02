package com.cmpe277.skibuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cmpe277.skibuddy.Utility.SessionManager;
import com.parse.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            setContentView(R.layout.activity_main);
            Button dashboardButton = (Button)findViewById(R.id.dashboardButton);
            dashboardButton.setOnClickListener(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.dashboardButton :
                Intent intent = new Intent(this.getApplicationContext(), DashboardActiity.class);
                startActivity(intent);
                break;
        }
    }
}
