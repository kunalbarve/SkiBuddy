package com.cmpe277.skibuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cmpe277.skibuddy.DAOs.RecordDao;
import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Record;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Swapnil on 12/3/2015.
 */
public class RecordListAdapter extends ArrayAdapter<Record> {

    Context context;
    List<Record> recordList;
    TextView recordName;

    public RecordListAdapter(Context context, int textViewResourceId, List<Record> recordList){
        super(context, textViewResourceId, recordList);
        this.context=context;
        this.recordList=recordList;
    }

    @Override
    public View getView(final int position, final View view, ViewGroup parent) {
        View row = null;
        try{
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row =inflater.inflate(R.layout.record_item, parent, false);
            recordName = (TextView)row.findViewById(R.id.recordNameRow);
            System.out.println("<====recordid "+recordList.get(position).getId());
            recordName.setText(recordList.get(position).getEventName()+" "+recordList.get(position).getStartTime()+" "+recordList.get(position).getEndTime());
            recordName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordDao.getRecordAndOpenIntent(recordList.get(position).getId().trim(), new ParseReceiveAsyncObjectListener() {
                        @Override
                        public void receiveObjects(HashMap<String, Object> objectMap) {
                            Intent displayRecordIntent = new Intent(context, DisplayRecordActivity.class);
                            Bundle extras = new Bundle();
                            extras.putSerializable("event", (Event) objectMap.get("event"));
                            extras.putSerializable("record", (Record) objectMap.get("record"));
                            displayRecordIntent.putExtras(extras);
                            context.startActivity(displayRecordIntent);
                        }
                    });
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return row;
    }


}



