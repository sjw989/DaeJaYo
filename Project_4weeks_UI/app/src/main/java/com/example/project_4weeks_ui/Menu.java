package com.example.project_4weeks_ui;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Menu {
    //int id;
    private String name;
    private String img_url;
    private String info1;
    private String info2;
    private String info3;

    public Menu() {
    }


    /*public int get_id(){
        return this.id;
    }*/
    public String get_img_URL() {
        return img_url;
    }

    public void set_img_URL(String img_url) {
        this.img_url = img_url;
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_info1() {
        return info1;
    }

    public void set_info1(String info1) {
        this.info1 = info1;
    }
    public String get_info2() {
        return info2;
    }

    public void set_info2(String info2) {
        this.info2 = info2;
    }
    public String get_info3() {
        return info3;
    }

    public void set_info3(String info3) {
        this.info3 = info3;
    }
}