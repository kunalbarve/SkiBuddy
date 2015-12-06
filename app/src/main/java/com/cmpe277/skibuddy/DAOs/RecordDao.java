package com.cmpe277.skibuddy.DAOs;

import android.content.Intent;
import android.util.Log;

import com.cmpe277.skibuddy.ListUtility.CallbackUtils;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;
import com.cmpe277.skibuddy.Models.User;
import com.cmpe277.skibuddy.ParseReceiveAsyncObjectListener;
import com.cmpe277.skibuddy.Utility.Constatnts;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by knbarve on 12/1/15.
 */
public class RecordDao {

    public static void insertRecord(Record record){
        ParseObject parseObject = new ParseObject("Record");
        parseObject.put("userId", record.getUserId());
        parseObject.put("eventId", record.getEventId());
        parseObject.put("distance", record.getDistance());
        parseObject.put("startTime", record.getStartTime());
        parseObject.put("endTime", record.getEndTime());
        parseObject.put("path", record.getPath());
        parseObject.put("totalTime", record.getTotalTime());
        parseObject.put("eventName", record.getEventName());
        parseObject.saveInBackground();
    }

    public static void getRecordAndOpenIntent(String recordId, final ParseReceiveAsyncObjectListener listnerForObjects){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
        final Record fetchedRecord = new Record();
        query.getInBackground(recordId, new GetCallback<ParseObject>() {
            public void done(ParseObject record, ParseException e) {
                if (e == null) {
                    fetchedRecord.setId(record.getString("id"));
                    fetchedRecord.setUserId(record.getString("userId"));
                    fetchedRecord.setEventId(record.getString("eventId"));
                    fetchedRecord.setDistance(record.getDouble("distance"));
                    fetchedRecord.setStartTime(record.getString("startTime"));
                    fetchedRecord.setEndTime(record.getString("endTime"));
                    fetchedRecord.setPath(record.getString("path"));
                    fetchedRecord.setTotalTime(record.getString("totalTime"));
                    //fetch event name for event id
                    EventDao.getEventDetailAndStartIntent(fetchedRecord.getEventId(), fetchedRecord, listnerForObjects);
                } else {
                    Log.d("records", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getRecordsForUserId(String userId, String eventId,final ParseReceiveAsyncObjectListener listnerForObjects){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Record");
        query.whereEqualTo("userId", userId);
        query.whereEqualTo("eventId", eventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(Constatnts.TAG, "Came in fetching record details" + objects.size());
                    ArrayList<Record> recordList = new ArrayList<Record>();
                    for (ParseObject obj : objects) {
                        Record record = new Record();
                        record.setEventName(obj.getString("eventName"));
                        record.setId(obj.getObjectId());
                        record.setUserId(obj.getString("userId"));
                        record.setEventId(obj.getString("eventId"));
                        record.setDistance(obj.getDouble("distance"));
                        record.setStartTime(obj.getString("startTime"));
                        record.setEndTime(obj.getString("endTime"));
                        record.setPath(obj.getString("path"));
                        record.setTotalTime(obj.getString("totalTime"));
                        recordList.add(record);
                    }
                    HashMap<String, Object> objectList = new HashMap<String, Object>();
                    objectList.put("records",recordList);
                    listnerForObjects.receiveObjects(objectList);
                } else {
                    Log.d("events", "Error: " + e.getMessage());
                }
            }
        });
    }

}
