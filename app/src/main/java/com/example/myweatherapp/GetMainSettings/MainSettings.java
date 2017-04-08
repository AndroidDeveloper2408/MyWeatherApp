package com.example.myweatherapp.GetMainSettings;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainSettings {

    public static String API_KEY = "177932916b83f4e3fca251f6366c70c5";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/forecast";

    @NonNull
    public static String apiRequest(){
        StringBuilder sb = new StringBuilder(API_LINK);
        sb.append("?q=London,uk&appid=177932916b83f4e3fca251f6366c70c5");
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
