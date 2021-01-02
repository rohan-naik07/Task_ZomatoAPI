package com.example.task_app.models;

import android.os.Parcelable;

import java.io.Serializable;

public class Restaurant implements Serializable {

    private String id ;
    private String name;
    private String url;
    private String photoUrl;
    private String address;
    private String cuisines;
    private String timings;
    private String phoneNumbers;
    private String menuUrl;
    private String latitude;
    private String longitude;
    private Integer costforTwo;
    private Float avgRating;
    private boolean hasOnlinedelivery;

    public Restaurant(String id, String name, String url, String photoUrl,
                      String address, String cuisines, String timings,
                      String phoneNumbers, String menuUrl, String latitude,
                      String longitude, Integer costforTwo, Float avgRating, boolean hasOnlinedelivery) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.photoUrl = photoUrl;
        this.address = address;
        this.cuisines = cuisines;
        this.timings = timings;
        this.phoneNumbers = phoneNumbers;
        this.menuUrl = menuUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.costforTwo = costforTwo;
        this.avgRating = avgRating;
        this.hasOnlinedelivery = hasOnlinedelivery;
    }
    public Restaurant(String id, String name, String url, String photoUrl,
                      String address, String cuisines, String menuUrl, String latitude,
                      String longitude, Integer costforTwo, Float avgRating, boolean hasOnlinedelivery) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.photoUrl = photoUrl;
        this.address = address;
        this.cuisines = cuisines;
        this.menuUrl = menuUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.costforTwo = costforTwo;
        this.avgRating = avgRating;
        this.hasOnlinedelivery = hasOnlinedelivery;
    }

    public Restaurant(String id, String name, String url, String photoUrl) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.photoUrl = photoUrl;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public Integer getCostforTwo() {
        return costforTwo;
    }

    public void setCostforTwo(Integer costforTwo) {
        this.costforTwo = costforTwo;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public boolean isHasOnlinedelivery() {
        return hasOnlinedelivery;
    }

    public void setHasOnlinedelivery(boolean hasOnlinedelivery) {
        this.hasOnlinedelivery = hasOnlinedelivery;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

/*location (ResLocation, optional): Restaurant location details ,
private Integer average_cost_for_two (integer, optional): Average price of a meal for two people
private String phone_numbers (string, optional): [Partner access] Restaurant's contact numbers in csv format ,
private String photoUrl (Array[Photo], optional): [Partner access] List of restaurant photos
     */

