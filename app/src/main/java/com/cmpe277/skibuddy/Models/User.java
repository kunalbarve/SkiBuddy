package com.cmpe277.skibuddy.Models;

import java.util.Date;

/**
 * Created by knbarve on 11/30/15.
 */
public class User {

    private String userId = "";
    private String userName = "";
    private String tagLine = "";
    private String url = "";
    private String image = "";
    private String latitude = "";
    private String longitude = "";
    private Date locationUpdateTime;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Date getLocationUpdateTime() {
        return locationUpdateTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLocationUpdateTime(Date locationUpdateTime) {
        this.locationUpdateTime = locationUpdateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", locationUpdateTime=" + locationUpdateTime +
                '}';
    }
}
