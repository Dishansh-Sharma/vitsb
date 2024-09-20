package com.example.vitsp.models;

public class ModelCategory {

    private String category;
    private int icon;

    public ModelCategory(String category, int icon) {
        this.category = category;
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
