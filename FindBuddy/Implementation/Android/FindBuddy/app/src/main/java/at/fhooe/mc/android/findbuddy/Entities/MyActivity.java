package at.fhooe.mc.android.findbuddy.Entities;

import java.io.Serializable;
import java.util.Date;


public class MyActivity implements Serializable{

    private String id;
    private String name;
    private String category;
    private Date startDate;
    private Date endDate;
    private int maxParticipants;
    private String information;
    private double latitude;
    private double longitude;
    private String creator;

    public MyActivity() { }

    public MyActivity(String id, String name, String category, Date startDate, Date endDate, int maxParticipants, String information, double latitude, double longitude, String creator) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creator = creator;
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

    public String getInformations() {
        return information;
    }

    public void setInformations(String informations) {
        this.information = informations;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
