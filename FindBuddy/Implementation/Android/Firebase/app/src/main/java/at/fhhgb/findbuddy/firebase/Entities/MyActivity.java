package at.fhhgb.findbuddy.firebase.Entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import at.fhhgb.findbuddy.firebase.MyCalendar;

public class MyActivity {

    private String id;
    private String name;
    private String category;
    private Date startDate;
    private Date endDate;
    private int maxParticipants;
    private String location;
    private double latitude;
    private double longitude;

    public MyActivity() { }

    public MyActivity(String id, String name, String category, Date startDate, Date endDate, int maxParticipants, String location, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
