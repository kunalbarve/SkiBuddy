package com.cmpe277.skibuddy.DAOs;

import android.util.Log;

import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.parse.*;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static void getUserDetails(List<String> userIds, final ParseReceiveAsyncObjectListener listenerObj){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
        query.whereContainedIn("userId", userIds);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG, "Came in Group callback:" + objects.size());
                    ArrayList<User> userDetails = new ArrayList<>();
                    for (ParseObject obj : objects) {
                        User user = new User();
                        user.setUserId(obj.getString("userId"));
                        user.setUrl(obj.getString("url"));
                        user.setTagLine(obj.getString("tagLine"));
                        user.setImage(obj.getString("image"));
                        user.setUserName(obj.getString("userName"));
                        userDetails.add(user);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setUserDetails(userDetails);
                    Log.d(Constatnts.TAG, "Callback set");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("user_callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }
}
