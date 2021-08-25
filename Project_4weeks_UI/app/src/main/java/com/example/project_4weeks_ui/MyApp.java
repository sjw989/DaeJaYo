package com.example.project_4weeks_ui;

import android.app.Application;

public class MyApp extends Application {
    public static String selected_category_KR; // 선택된카테고리 한글
    public static String selected_category_ENG;
    public static boolean tagCheck= false;

    public String getCategory_KR() {
        return selected_category_KR;
    }

    public void setCategory_KR( String category ) {
        this.selected_category_KR = category;
    }
    public String getCategory_ENG() {
        return selected_category_ENG;
    }

    public void setCategory_ENG( String category ) {
        this.selected_category_ENG = category;
    }
    public boolean getTagCheck(){
        return tagCheck;
    }
    public void setTagCheck(boolean tagCheck){
        this.tagCheck = tagCheck;
    }
}
