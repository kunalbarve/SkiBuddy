package com.cmpe277.skibuddy.Models;

import java.util.Date;

/**
 * Created by knbarve on 11/30/15.
 */
public class User {

    private String userId;
    private String userName;
    private String tagLine;
    private String url;
    private String latitude;
    private String longitude;
    private Date locationUpdateTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getLocationUpdateTime() {
        return locationUpdateTime;
    }

    public void setLocationUpdateTime(Date locationUpdateTime) {
        this.locationUpdateTime = locationUpdateTime;
    }
}
