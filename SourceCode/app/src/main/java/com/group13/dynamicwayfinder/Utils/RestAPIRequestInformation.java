package com.group13.dynamicwayfinder.Utils;


import com.group13.dynamicwayfinder.Utils.UserSettings.UserSettings;

public class RestAPIRequestInformation {
    private int id;
    private String name;
    double startLocationLat;
    double startLocationLong;
    double endLocationLat;
    double endLocationLong;
    private UserSettings settings;

    public RestAPIRequestInformation(int id, String name, String startLocationLat, String startLocationLong, String endLocationLat, String endLocationLong, UserSettings settings){
        this.id = id;
        this.name = name;
        this.startLocationLat = Double.parseDouble(startLocationLat);
        this.startLocationLong = Double.parseDouble(startLocationLong);
        this.endLocationLat = Double.parseDouble(endLocationLat);
        this.endLocationLong = Double.parseDouble(endLocationLong);
        this.settings = settings;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getStartLocationLat() {
        return startLocationLat;
    }

    public void setStartLocationLat(double startLocationLat) {
        this.startLocationLat = startLocationLat;
    }

    public double getStartLocationLong() {
        return startLocationLong;
    }

    public void setStartLocationLong(double startLocationLong) {
        this.startLocationLong = startLocationLong;
    }

    public double getEndLocationLat() {
        return endLocationLat;
    }

    public void setEndLocationLat(double endLocationLat) {
        this.endLocationLat = endLocationLat;
    }

    public double getEndLocationLong() {
        return endLocationLong;
    }

    public void setEndLocationLong(double endLocationLong) {
        this.endLocationLong = endLocationLong;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }
}
