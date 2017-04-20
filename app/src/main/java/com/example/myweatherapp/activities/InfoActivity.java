package com.example.myweatherapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.myweatherapp.R;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.myweatherapp.getMainSettings.MainSettings.convertDate;
import static com.example.myweatherapp.getMainSettings.MainSettings.setIconToList;
import static com.example.myweatherapp.getMainSettings.MainSettings.unixTimeStampToDateTime;

public class InfoActivity extends AppCompatActivity {


    SimpleAdapter simpleAdapter;

    ListView listView;

    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;

    public int var;

    MainSettings mainSettings = new MainSettings();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        var = intent.getIntExtra("intent", 10);
        listView = (ListView)findViewById(R.id.lvInfo);
        textView = (TextView) findViewById(R.id.tvDay);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("isdata", false)) {
            fillList(mainSettings.parseLongForecastJson(prefs.getString("lastWeek", "123")), var);
        }
    }

    public void fillList(ArrayList<ArrayList<Weather>> grouppedArrayList, int a){
        // упаковываем данные в понятную для адаптера структуру
        data = new ArrayList<>();
        for (int i = 0; i < grouppedArrayList.get(a).size(); i++) {
            m = new HashMap<>();

            m.put("time", unixTimeStampToDateTime(grouppedArrayList.get(a).get(i).getDateTimestamp(), this));
            m.put("description", grouppedArrayList.get(a).get(i).getDescription());
            m.put("wind", "Wind:  " + grouppedArrayList.get(a).get(i).getWind() + " m/s");
            m.put("pressure",  "Pressure: " + grouppedArrayList.get(a).get(i).getPressure() + " hPa");
            m.put("humidity", "Humidity: " + grouppedArrayList.get(a).get(i).getHumidity() + " %");
            m.put("icon", setIconToList(grouppedArrayList.get(a).get(i).getIcon()));
            m.put("temperature", String.format("%.1f °C", grouppedArrayList.get(a).get(i).getTemperature()));
            data.add(m);
        }
        textView.setText(convertDate(grouppedArrayList.get(a).get(0).getDateTimestamp()));
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = {"time", "description", "wind", "pressure", "humidity", "icon", "temperature"};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {R.id.itemDate, R.id.itemDescription, R.id.itemWind, R.id.itemPressure,
                R.id.itemHumidity, R.id.itemIcon, R.id.itemTemperature};

        // создаем адаптер
        simpleAdapter = new SimpleAdapter(this, data, R.layout.list_row_cardview, from, to);

        // определяем список и присваиваем ему адаптер
        listView.setAdapter(simpleAdapter);
    }
}