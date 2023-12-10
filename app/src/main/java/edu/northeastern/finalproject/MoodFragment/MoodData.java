package edu.northeastern.finalproject.MoodFragment;

import java.util.Date;

public class MoodData {
    private String email;

    private int moodValue;
    private Date date;
    private String dayOfWeek;

    public MoodData() {
    }

    public MoodData(String email, int moodValue, Date date, String dayOfWeek) {
        this.email = email;
        this.moodValue = moodValue;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
    }

    public int getMoodValue() {
        return moodValue;
    }

    public void setMoodValue(int moodValue) {
        this.moodValue = moodValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfMonth() {
        return date.getMonth();
    }

    public String toString() {
        return "email: " + email + "moodValue: " + moodValue + "date: " + date + "dayofweek: " + dayOfWeek;
    }
}

