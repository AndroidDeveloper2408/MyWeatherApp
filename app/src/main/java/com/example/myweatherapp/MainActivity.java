package com.example.myweatherapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.myweatherapp.GetMainSettings.GetConnection;
import com.example.myweatherapp.GetMainSettings.MainSettings;
import com.example.myweatherapp.GetMainSettings.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Weather> longTermTomorrowWeather = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetWeather().execute(MainSettings.apiRequest());
    }

    public static String getRainString(JSONObject rainObj) {
        String rain = "0";
        if (rainObj != null) {
            rain = rainObj.optString("3h", "fail");
            if ("fail".equals(rain)) {
                rain = rainObj.optString("1h", "0");
            }
        }
        return rain;
    }

    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = "1";
            } else {
                icon = "2";
            }
        } else {
            switch (id) {
                case 2:
                    icon = "3";
                    break;
                case 3:
                    icon = "4";
                    break;
                case 7:
                    icon = "5";
                    break;
                case 8:
                    icon = "6";
                    break;
                case 6:
                    icon = "7";
                    break;
                case 5:
                    icon = "8";
                    break;
            }
        }
        return icon;
    }

    private class GetWeather extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            GetConnection http = new GetConnection();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                int i;
                JSONObject reader = new JSONObject(s);
                final String code = reader.optString("cod");

                longTermTomorrowWeather = new ArrayList<>();

                JSONArray list = reader.getJSONArray("list");

                for (i = 0; i < list.length(); i++) {
                    Weather weather = new Weather();

                    JSONObject listItem = list.getJSONObject(i);
                    JSONObject main = listItem.getJSONObject("main");

                    weather.setDate(listItem.getString("dt"));
                    weather.setTemperature(main.getString("temp"));
                    weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                    JSONObject windObj = listItem.optJSONObject("wind");
                    if (windObj != null) {
                        weather.setWind(windObj.getString("speed"));
                        weather.setWindDirectionDegree(windObj.getDouble("deg"));
                    }
                    weather.setPressure(main.getString("pressure"));
                    weather.setHumidity(main.getString("humidity"));

                    JSONObject rainObj = listItem.optJSONObject("rain");
                    String rain = "";
                    if (rainObj != null) {
                        rain = getRainString(rainObj);
                    } else {
                        JSONObject snowObj = listItem.optJSONObject("snow");
                        if (snowObj != null) {
                            rain = getRainString(snowObj);
                        } else {
                            rain = "0";
                        }
                    }
                    weather.setRain(rain);

                    final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                    weather.setId(idString);

                    final String dateMsString = listItem.getString("dt") + "000";
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(dateMsString));
                    weather.setIcon(setWeatherIcon(Integer.parseInt(idString), cal.get(Calendar.HOUR_OF_DAY)));
                    longTermTomorrowWeather.add(weather);
                }
                Log.e("parseJson", longTermTomorrowWeather.get(2).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}