package com.example.task_app.models;

public class Restaurant {

    private String id ;
    private String name;
    private String url;
    private String photoUrl;

    public Restaurant(String id, String name, String url, String photoUrl) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.photoUrl = photoUrl;
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

