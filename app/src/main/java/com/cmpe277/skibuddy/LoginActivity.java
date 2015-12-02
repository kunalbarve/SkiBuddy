package com.cmpe277.skibuddy;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.SessionManager;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final int RC_SIGN_IN = 0;
    private static final int REQ_SIGN_IN_REQUIRED = 12;

    private GoogleApiClient mGoogleApiClient;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_IN_REQUIRED) {
            //new GetAccessToken().execute("");
        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;
        if(!session.isLoggedIn() && session.isDisconnected()){
            User user = getUserDetails();
            if(user != null){
                session.setIsDisconnected(false);
                session.createLoginSession(user);


                finish();
            }
            //new GetAccessToken().execute("");
        }else{
            onSignOutClicked();
            session.setIsDisconnected(true);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private class GetAccessToken extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accessToken = null;
            try {
                accessToken = GoogleAuthUtil.getToken(getApplicationContext(), session.getLoggedInMail(), Constatnts.SCOPE_STRING);
                session.setToken(accessToken);
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (Exception e) {
                Log.e(Constatnts.TAG, "Exception in getting token", e);
            }
            return accessToken;
        }

    }

    private User getUserDetails(){
        User user = null;
        try{
            if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                user = new User();

                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                user.setUserId(email);

                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                if(person.hasDisplayName())
                    user.setUserName(person.getDisplayName());

                if(person.hasTagline())
                    user.setTagLine(person.getTagline());

                if(person.hasImage())
                    user.setImage(person.getImage().getUrl());

                if(person.hasUrl())
                    user.setUrl(person.getUrl());


                Log.d(Constatnts.TAG, user.toString());

            }else{
                Utilities.shortMsg(getApplicationContext(), "User details are not available.");
            }
        }catch (Exception e){
            Log.e(Constatnts.TAG, e.getMessage());
            Utilities.shortMsg(getApplicationContext(), "Not able to fetch user details.");
        }
        return user;
    }
}
