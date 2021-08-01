package com.example.project_4weeks_ui;

import android.location.Location;

import java.util.ArrayList;

public class WeatherData {
    Location coord;
    ArrayList<Wet> weather = new ArrayList<Wet>();
    String base;
    TemperatureData main;
    String visibility;
    String speed;
    String deg;
    String gust;
    String all;
    String dt;
    String type;
    String id;
    String country;
    String sunrise;
    String sunset;
    String timezone;
    String name;
    String cod;
    public class Wet {
        String id;
        String main;
        String description;
        String icon;
    }
    public class TemperatureData {
        String temp;
        String feels_like;
        String temp_min;
        String temp_max;
        String pressure;
        String humidity;
        String sea_level;
        String grnd_level;
    }
}
