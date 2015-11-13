package com.cmpe277.skibuddy.Utility;

import android.app.Application;
import com.parse.*;

public class ParseInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Constatnts.APPLICATION_ID, Constatnts.CLIENT_KEY);
    }
}
