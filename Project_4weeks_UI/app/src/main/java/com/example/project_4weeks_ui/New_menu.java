package com.example.project_4weeks_ui;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class New_menu {
    String name;
    String img;
    String id;
    Information info;
    ArrayList<Ingredient> ingre;
    ArrayList<add_recipe> recipe;

    public New_menu(String name, String img, String id, Information info, ArrayList<Ingredient> ingre, ArrayList<add_recipe> recipe) {
        this.name = name;
        this.img = img;
        this.id = id;
        this.info = info;
        this.ingre = ingre;
        this.recipe = recipe;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public Information getInfo() {
        return info;
    }

    public void setInfo(Information info) {
        this.info = info;
    }

    public void setName(String id) {
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Ingredient> getIngre() {
        return ingre;
    }

    public void setIngre(ArrayList<Ingredient> ingre) {
        this.ingre = ingre;
    }

    public ArrayList<add_recipe> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<add_recipe> recipe) {
        this.recipe = recipe;
    }

}
