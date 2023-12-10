package edu.northeastern.finalproject.data;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

public class UserDailyRecord {
    private String userId;
    private Date date;
    private Integer mood;
    private List<String> photoUrls; // URLs to photos in Firebase Storage
    private int steps;
    private double heartRate;

    public String getDailyQuote() {
        return dailyQuote;
    }

    public void setDailyQuote(String dailyQuote) {
        this.dailyQuote = dailyQuote;
    }

    private String dailyQuote;

    // Default constructor is needed for Firestore's automatic data mapping
    public UserDailyRecord() {
    }

    // Constructor with parameters
    public UserDailyRecord(String userId, Date date, Integer mood, List<String> photoUrls, int steps, double heartRate, String dailyQuote) {
        this.userId = userId;
        this.date = date;
        this.mood = mood;
        this.photoUrls = photoUrls;
        this.steps = steps;
        this.heartRate = heartRate;
        this.dailyQuote = dailyQuote;
    }

    // Getters and setters for each field for Firestore's automatic data mapping
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(Integer mood) {
        this.mood = mood;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return "UserDailyRecord{" +
                "userId='" + userId + '\'' +
                ", date=" + date +
                ", photoUrls=" + photoUrls +
                ", dailyQuote=" + dailyQuote;
    }
}
