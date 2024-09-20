package com.example.vitsp.models;

public class ModelAd {

    String id;
    String uid;
    String brand;
    String category;
    String condition;
    String description;
    String price;
    String title;
    String status;
    long timestamp;
    boolean favorite;



    public ModelAd() {
    }

    public ModelAd(String id, String uid, String brand, String category, String condition, String description, String price, String title, String status, long timestamp, boolean favorite) {
        this.id = id;
        this.uid = uid;
        this.brand = brand;
        this.category = category;
        this.condition = condition;
        this.description = description;
        this.price = price;
        this.title = title;
        this.status = status;
        this.timestamp = timestamp;
        this.favorite = favorite;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}


