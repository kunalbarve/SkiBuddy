package com.cmpe277.skibuddy.DAOs;

import android.content.Context;
import android.util.Log;

import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.cmpe277.skibuddy.Utility.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class GroupDao {

    public static void getGroupDetails(String userId, final ParseReceiveAsyncObjectListener listenerObj){
        Log.d(Constatnts.TAG, "Came in first call");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Groups");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG, "Came in Group callback:"+objects.size());
                    List<Group> groups = new ArrayList<>();
                    for (ParseObject obj : objects) {
                        Group group = new Group();
                        group.setEventId(obj.getString("eventId"));
                        group.setId(obj.getObjectId());
                        group.setUserId(obj.getString("userId"));
                        group.setStatus(obj.getString("flag"));
                        groups.add(group);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setGroupDetails(groups);
                    Log.d(Constatnts.TAG, "Callback set");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getGroupDetailsForEvents(String eventId, final ParseReceiveAsyncObjectListener listenerObj){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Groups");
        query.whereEqualTo("eventId", eventId);
        query.whereEqualTo("flag","1");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG, "Came in Group callback:" + objects.size());
                    List<Group> groups = new ArrayList<>();
                    for (ParseObject obj : objects) {
                        Group group = new Group();
                        group.setEventId(obj.getString("eventId"));
                        group.setId(obj.getObjectId());
                        group.setUserId(obj.getString("userId"));
                        group.setStatus(obj.getString("flag"));
                        groups.add(group);
                    }

                    CallbackUtils callbackUtils = new CallbackUtils();
                    callbackUtils.setGroupDetails(groups);
                    Log.d(Constatnts.TAG, "Callback set");
                    HashMap<String, Object> objectMap = new HashMap<>();
                    objectMap.put("event_group_callback", callbackUtils);
                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void addUserToEvent(Event event, String emailAddress, Context context) {
        UserDao.checkUserAvailableForEvent(emailAddress);
        createGroup(event.getId(), emailAddress, context);
    }

    private static void createGroup(final String eventId, final String userId, final Context context){
        ParseObject group = new ParseObject("Groups");
        group.put("userId", userId);
        group.put("eventId", eventId);
        group.put("flag", "0");
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Send Notification and mail
                    Utilities.shortMsg(context, "User added successfully to the event.");
                } else {
                    Log.e(Constatnts.TAG, e.getMessage());
                }
            }
        });

    }


    public static void updateGroup(String eventId, String userId, final String mode, final ParseReceiveAsyncObjectListener listenerObj) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Groups");
        query.whereEqualTo("eventId", eventId);
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    HashMap<String, Object> objectMap = new HashMap<>();
                    if (objects.size() > 0) {
                        ParseObject group = objects.get(0);
                        if (mode.equalsIgnoreCase(Constatnts.SUBSCRIBE_MODE)){
                            group.put("flag", "1");
                        }else if(mode.equalsIgnoreCase(Constatnts.UN_SUBSCRIBE_MODE)){
                            group.put("flag", "0");
                        }
                        group.saveInBackground();
                        objectMap.put("update_group_callback", true);
                    }else{
                        objectMap.put("update_group_callback", false);
                    }

                    listenerObj.receiveObjects(objectMap);

                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }
}
