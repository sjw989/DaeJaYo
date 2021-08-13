package com.example.project_4weeks_ui;

public class RecipeItem {
    String name;
    String url;

    public RecipeItem(String url, String name) {
        this.name = name;
        this.url = url;
    }

    public String getResourceId() {
        return url;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setResourceId(String url) {
        this.url = url;
    }
}