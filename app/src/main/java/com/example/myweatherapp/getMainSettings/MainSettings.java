package com.example.myweatherapp.getMainSettings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainSettings {

    public static String API_KEY = "177932916b83f4e3fca251f6366c70c5";
    public static String API_LINK_TODAY = "http://api.openweathermap.org/data/2.5/weather";
    public static String API_LINK_WEEK = "http://api.openweathermap.org/data/2.5/forecast";

    @NonNull
    public static String apiRequestToday(){
        StringBuilder sb = new StringBuilder(API_LINK_TODAY);
        sb.append("?q=London,uk&appid=");
        sb.append(API_KEY);
        return sb.toString();
    }

    @NonNull
    public static String apiRequestWeek(){
        StringBuilder sb = new StringBuilder(API_LINK_WEEK);
        sb.append("?q=London,uk&appid=");
        sb.append(API_KEY);
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(Date date, Context context){
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        return timeFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static int setIconToList(String id){
        switch(id){
            case "01d":
                return R.drawable.icon_01d;
            case "01n":
                return R.drawable.icon_01n;
            case "02d":
                return R.drawable.icon_02d;
            case "02n":
                return R.drawable.icon_02n;
            case "03d":
                return R.drawable.icon_03d;
            case "03n":
                return R.drawable.icon_03n;
            case "04d":
                return R.drawable.icon_04d;
            case "04n":
                return R.drawable.icon_04n;
            case "09d":
                return R.drawable.icon_09d;
            case "09n":
                return R.drawable.icon_09n;
            case "10d":
                return R.drawable.icon_10d;
            case "10n":
                return R.drawable.icon_10n;
            case "11d":
                return R.drawable.icon_11d;
            case "11n":
                return R.drawable.icon_11n;
            case "13d":
                return R.drawable.icon_13d;
            case "13n":
                return R.drawable.icon_13n;
            case "50d":
                return R.drawable.icon_50d;
            case "50n":
                return R.drawable.icon_50n;
        }
        return 0;
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String convertDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static String equalsTemp(double temp1, double temp2){
        if(temp1 == temp2)
            return String.format("%.1f °C", temp1);
        else
            return String.format("%.1f - %.1f °C", temp1, temp2);
    }

    public boolean isSameDate(Date date1, Date date2) {
        return date1.getDay() == date2.getDay();
    }

    public ArrayList<ArrayList<Weather>> groupWeatherByDay(List<Weather> longTermWeather) {

        ArrayList<ArrayList<Weather>> arrayList = new ArrayList<>();

        arrayList.add(new ArrayList<Weather>());
        arrayList.get(0).add(longTermWeather.get(0));

        Date prev = longTermWeather.get(0).getDateTimestamp();

        for (int i = 1; i < longTermWeather.size(); i++) {
            Date current = longTermWeather.get(i).getDateTimestamp();
            int lastGroup = arrayList.size() - 1;

            if (isSameDate(prev, current)) {
                arrayList.get(lastGroup).add(longTermWeather.get(i));
            } else {
                prev = current;
                arrayList.add(new ArrayList<Weather>());
                arrayList.get(lastGroup + 1).add(longTermWeather.get(i));
            }
        }

        return arrayList;
    }

    public ArrayList<ArrayList<Weather>> parseLongForecastJson(String result, Context context) {
        ArrayList<ArrayList<Weather>> grouppedArrayList = new ArrayList<>();
        try {
            int i;

            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                Toast.makeText(context, "CITY_NOT_FOUND", Toast.LENGTH_SHORT).show();
            }
            List<Weather> todayWeatherList = new ArrayList<>();

            JSONArray list = reader.getJSONArray("list");

            for (i = 0; i < list.length(); i++) {
                Weather weather = new Weather();

                JSONObject cityObj = reader.getJSONObject("city");
                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem.getJSONObject("main");

                weather.setCity(cityObj.getString("name"));
                weather.setDate(listItem.getString("dt_txt"));
                weather.setDateTimestamp(listItem.getString("dt"));
                weather.setTime(listItem.getString("dt_txt"));
                weather.setTemperature(main.getString("temp"));
                weather.setMin_temperature(main.getString("temp_min"));
                weather.setMax_temperature(main.getString("temp_max"));
                weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                JSONObject windObj = listItem.optJSONObject("wind");
                if (windObj != null) {
                    weather.setWind(windObj.getString("speed"));
                    weather.setWindDirectionDegree(windObj.getDouble("deg"));
                }
                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));

                final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                weather.setId(idString);

                weather.setIcon(listItem.optJSONArray("weather").getJSONObject(0).getString("icon"));
                todayWeatherList.add(weather);
            }
            grouppedArrayList = groupWeatherByDay(todayWeatherList);
        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            Toast.makeText(context, "JSON_EXCEPTION", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, "CITY FOUND SUCESS", Toast.LENGTH_SHORT).show();
        return grouppedArrayList;
    }

    public ArrayList<Weather> groupByDay(ArrayList<ArrayList<Weather>> itemList){
        ArrayList<Weather> arrayList = new ArrayList<>();
        Double min_temp = 0.0;
        Double max_temp = 0.0;
        Double wind = 0.0;
        Double pressure = 0.0;
        Double humidity = 0.0;
        for(int i = 0; i <itemList.size(); i++){
            for (int j = 0; j < itemList.get(i).size(); j++){
                min_temp += itemList.get(i).get(j).getMin_temperature();
                max_temp += itemList.get(i).get(j).getMax_temperature();
                wind += Double.valueOf(itemList.get(i).get(j).getWind());
                pressure += Double.valueOf(itemList.get(i).get(j).getPressure());
                humidity += Double.valueOf(itemList.get(i).get(j).getHumidity());
            }
            Date date = itemList.get(i).get(0).getDateTimestamp();
            String icon = itemList.get(i).get(0).getIcon();
            String description = itemList.get(i).get(0).getDescription();
            Weather weather = new Weather();
            weather.setMin_temperature(min_temp/itemList.get(i).size());
            weather.setMax_temperature(max_temp/itemList.get(i).size());
            weather.setWind(String.valueOf(wind/itemList.get(i).size()));
            weather.setPressure(String.valueOf(pressure/itemList.get(i).size()));
            weather.setHumidity(String.valueOf(humidity/itemList.get(i).size()));
            weather.setDateTimestamp(date);
            weather.setIcon(icon);
            weather.setDescription(description);
            arrayList.add(i, weather);
            min_temp = 0.0;
            max_temp = 0.0;
            wind = 0.0;
            pressure = 0.0;
            humidity = 0.0;
        }
        return arrayList;
    }
}
