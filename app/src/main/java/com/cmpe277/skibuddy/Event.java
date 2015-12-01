package com.cmpe277.skibuddy;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by kvohra on 11/30/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject{

    public String getEventName(){
        return getString("eventName");
    }

    public void setEventName(String eventName){
        put("eventName",eventName);
    }

    public String getDescription()
    {
        return getString("description");
    }

    public void setDescription(String description)
    {
        put("description",description);
    }

   /* @Override
    public String toString()
    {
        return getString("eventName") + "\n" + getString("description");
    }*/
}
