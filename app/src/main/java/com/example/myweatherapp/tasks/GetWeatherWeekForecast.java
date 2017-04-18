package com.example.myweatherapp.tasks;

import android.os.AsyncTask;

import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;

import java.util.ArrayList;

public abstract class GetWeatherWeekForecast extends AsyncTask<String,Void,String> {

    ArrayList<Weather> weathers =  new ArrayList<>();
    MainSettings mainSettings = new MainSettings();

    public abstract void onSucess(ArrayList<Weather> weatherArrayList, String s);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        GetConnection http = new GetConnection();
        return http.getJsonObj(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        weathers = mainSettings.groupByDay(mainSettings.parseLongForecastJson(s));
        onSucess(weathers, s);
    }
}
