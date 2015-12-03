package com.cmpe277.skibuddy.ListUtility;

import com.cmpe277.skibuddy.Models.Event;
import com.cmpe277.skibuddy.Models.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knbarve on 12/3/15.
 */
public class CallbackUtils {

    private List<Group> groupDetails;
    private ArrayList<Event> eventDetails;

    public List<Group> getGroupDetails() {
        return groupDetails;
    }

    public void setGroupDetails(List<Group> groupDetails) {
        this.groupDetails = groupDetails;
    }

    public ArrayList<Event> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(ArrayList<Event> eventDetails) {
        this.eventDetails = eventDetails;
    }
}
