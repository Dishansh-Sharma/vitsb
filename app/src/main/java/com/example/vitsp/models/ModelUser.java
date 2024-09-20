package com.example.vitsp.models;

public class ModelUser {
    private String uid;
    private String name;
    private String phone;
    private String profileImage;

    // Default constructor required for calls to DataSnapshot.getValue(ModelUser.class)
    public ModelUser() {
    }

    public ModelUser(String uid, String name, String phone, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.profileImage = profileImage;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
