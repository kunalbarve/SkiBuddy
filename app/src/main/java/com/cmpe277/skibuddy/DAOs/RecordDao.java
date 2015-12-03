package com.cmpe277.skibuddy.DAOs;

import android.content.Intent;
import android.util.Log;

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

}
