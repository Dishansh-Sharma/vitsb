package com.example.vitsp.models;

public class ModelImageSlider {
    private String imageUrl;

    // Default constructor (required for Firebase)
    public ModelImageSlider() {
    }

    // Constructor that accepts a String parameter
    public ModelImageSlider(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter and Setter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
