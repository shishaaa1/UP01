package com.example.boobleproject;

public class Achievement {

    public String title;
    public String description;
    public int iconRes;
    public boolean completed;
    public int percent;

    public Achievement(String title, String description,
                       int iconRes, boolean completed, int percent) {
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
        this.completed = completed;
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }
}
