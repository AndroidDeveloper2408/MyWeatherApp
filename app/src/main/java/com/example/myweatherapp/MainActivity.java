package com.example.myweatherapp;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;

    private List<Weather> longTermTomorrowWeather = new ArrayList<>();
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tabLayout = (TabLayout)findViewById(R.id.tabs);

        //Control
        /*txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);*/

        new GetWeather().execute(MainSettings.apiRequestWeek());

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

            if(isSameDate(prev, current)) {
                arrayList.get(lastGroup).add(longTermWeather.get(i));
            } else {
                prev = current;
                arrayList.add(new ArrayList<Weather>());
                arrayList.get(lastGroup+1).add(longTermWeather.get(i));
            }
        }

        return arrayList;
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

                    JSONObject cityObj = reader.getJSONObject("city");
                    JSONObject listItem = list.getJSONObject(i);
                    JSONObject main = listItem.getJSONObject("main");

                            weather.setCity(cityObj.getString("name"));
                    weather.setDate(listItem.getString("dt_txt"));
                    weather.setDateTimestamp(listItem.getString("dt"));
                    weather.setTime(listItem.getString("dt_txt"));
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

                    weather.setIcon(listItem.optJSONArray("weather").getJSONObject(0).getString("icon"));
                    longTermTomorrowWeather.add(weather);
                }

                txtCity.setText(longTermTomorrowWeather.get(0).getCity().toString());
                txtLastUpdate.setText(String.format("Last Updated: %s", MainSettings.getDateNow()));
                txtDescription.setText(longTermTomorrowWeather.get(0).getDescription().toString());
                txtHumidity.setText(longTermTomorrowWeather.get(0).getHumidity().toString() + "%");
                txtTime.setText(longTermTomorrowWeather.get(0).getDate().toString());
                txtCelsius.setText(String.format("%.2f °C", longTermTomorrowWeather.get(0).getTemperature()));
                Picasso.with(MainActivity.this)
                        .load(MainSettings.getImage(longTermTomorrowWeather.get(0).getIcon()))
                        .into(imageView);
                ArrayList<ArrayList<Weather>> arrayList = groupWeatherByDay(longTermTomorrowWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}