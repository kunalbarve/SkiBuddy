package com.cmpe277.skibuddy.DAOs;

import android.util.Log;

import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.Models.Group;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
}
