package com.example.project_4weeks_ui;

public class Ingredient {
    String ingre_num;
    String ingre_count;
    String ingre_name;
    String ingre_unit;

    public Ingredient(String ingre_num, String ingre_name, String ingre_count, String ingre_unit) {
        this.ingre_num = ingre_num;
        this.ingre_count = ingre_count;
        this.ingre_name = ingre_name;
        this.ingre_unit = ingre_unit;
    }

    public String getIngre_num() {
        return ingre_num;
    }

    public void setIngre_num(String ingre_num) {
        this.ingre_num = ingre_num;
    }

    public String getIngre_count() {
        return ingre_count;
    }

    public void setIngre_count(String ingre_count) {
        this.ingre_count = ingre_count;
    }

    public String getIngre_name() {
        return ingre_name;
    }

    public void setIngre_name(String ingre_name) {
        this.ingre_name = ingre_name;
    }

    public String getIngre_unit() {
        return ingre_unit;
    }

    public void setIngre_unit(String ingre_unit) {
        this.ingre_unit = ingre_unit;
    }
}


