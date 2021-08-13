package com.example.project_4weeks_ui;

public class add_recipe {
    String num;
    String img;
    String txt;

    public add_recipe(String num, String img, String txt) {
        this.img = img;
        this.txt = txt;
        this.num = num;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getTxt() {
        return this.txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
