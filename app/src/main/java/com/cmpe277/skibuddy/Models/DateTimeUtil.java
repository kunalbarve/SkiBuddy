package com.cmpe277.skibuddy.Models;

/**
 * Created by knbarve on 12/2/15.
 */
public class DateTimeUtil {

    private int hour;
    private int minutes;
    private int day;
    private int month;
    private int year;

    public DateTimeUtil(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    public DateTimeUtil(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "DateTimeUtil{" +
                "hour=" + hour +
                ", minutes=" + minutes +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
