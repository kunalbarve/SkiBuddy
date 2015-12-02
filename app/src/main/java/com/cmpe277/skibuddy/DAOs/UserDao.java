package com.cmpe277.skibuddy.DAOs;

import android.util.Log;

import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.parse.*;

import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class UserDao {

    public static void checkAndUpdateUser(final User user){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("userId", user.getUserId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ParseObject person = null;
                if (e == null && objects != null) {
                    if(objects.size() == 0){
                        person = new ParseObject("User");
                        person.put("userId", user.getUserId());
                    }else{
                        person = objects.get(0);
                    }
                    person.put("userName", user.getUserName());
                    person.put("tagLine", user.getTagLine());
                    person.put("image", user.getImage());
                    person.put("url", user.getUrl());
                    person.put("latitude", user.getLatitude());
                    person.put("longitude", user.getLongitude());
                    person.put("locationUpdateTime", user.getLocationUpdateTime());
                    person.saveInBackground();
                } else {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });
    }
}
