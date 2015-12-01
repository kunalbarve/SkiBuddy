package com.cmpe277.skibuddy.Utility;

import android.app.Application;

import com.cmpe277.skibuddy.Event;
import com.parse.*;

public class ParseInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Event.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Constatnts.APPLICATION_ID, Constatnts.CLIENT_KEY);
    }
}
