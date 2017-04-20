package com.example.myweatherapp.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.myweatherapp.R;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Weather> weather = new ArrayList<>();
    ArrayList<ArrayList<Weather>> weathers = new ArrayList<>();
    MainSettings mainSettings = new MainSettings();

    ArrayList<String> dataTimeFrom = new ArrayList<>();
    ArrayList<String> dataTimeTo = new ArrayList<>();

    ArrayAdapter<String> adapterTimeFrom;
    ArrayAdapter<String> adapterTimeTo;

    int timeOfDayFrom = 0;
    int timeOfDayTo = 0;

    int posDayFrom = 0;
    int posDayTo = 0;

    int posTimeFrom = 0;
    int posTimeTo = 0;

    LineChart lineChart;

    Button button;

    LinearLayout graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graph = (LinearLayout)findViewById(R.id.graph);

        lineChart = (LineChart) findViewById(R.id.lineChart);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("isdata", false)) {
            weather = mainSettings.groupByDay(mainSettings.parseLongForecastJson(prefs.getString("lastWeek", "123")));
            weathers = mainSettings.parseLongForecastJson(prefs.getString("lastWeek", "123"));
            spinnerDayFrom();
            spinnerDayTo();
            spinnerTimeFrom();
            spinnerTimeTo();
            button = (Button) findViewById(R.id.button2);
            button.setOnClickListener(this);
        }
    }

    public int getEntryPos(String day){
        switch (day){
            case "0:00":
                return 0;
            case "3:00":
                return 1;
            case "6:00":
                return 2;
            case "9:00":
                return 3;
            case "12:00":
                return 4;
            case "15:00":
                return 5;
            case "18:00":
                return 6;
            case "21:00":
                return 7;
        }
        return 0;
    }

    public int getMyColor(int pos) {
        switch (pos) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.RED;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.BLACK;
            case 5:
                return Color.GRAY;
        }
        return 0;
    }

    public void temperatureGraph() {
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        int out = (posDayTo + 1);
        int in = (posTimeTo + 1);

        for (int q = posDayFrom; q < out; q++) {
            ArrayList<Entry> entries = new ArrayList<>();
            if (posDayFrom == posDayTo) {
                if (q == 0) {
                    for (int w = posTimeFrom; w < 9; w++) {
                        if (weathers.get(q).size() > w) {
                            entries.add(new Entry(Float.valueOf(String.valueOf(weathers.get(q).get(w).getTemperature())),
                                    getEntryPos(MainSettings.unixTimeStampToDateTime(weathers.get(q).get(w).getDateTimestamp(), this))));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(entries, getDateLabel(weather.get(q).getDateTimestamp()));
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setColor(getMyColor(q));
                    lineDataSets.add(lineDataSet);
                } else if (q == posDayTo) {
                    for (int w = posTimeFrom; w < in; w++) {
                        if (weathers.get(q).size() > w) {
                            entries.add(new Entry(Float.valueOf(String.valueOf(weathers.get(q).get(w).getTemperature())),
                                    getEntryPos(MainSettings.unixTimeStampToDateTime(weathers.get(q).get(w).getDateTimestamp(), this))));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(entries, getDateLabel(weather.get(q).getDateTimestamp()));
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setColor(getMyColor(q));
                    lineDataSets.add(lineDataSet);
                }
            } else {
                if (q == posDayFrom) {
                    for (int w = posTimeFrom; w < 9; w++) {
                        if (weathers.get(q).size() > w) {
                            entries.add(new Entry(Float.valueOf(String.valueOf(weathers.get(q).get(w).getTemperature())),
                                    getEntryPos(MainSettings.unixTimeStampToDateTime(weathers.get(q).get(w).getDateTimestamp(), this))));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(entries, getDateLabel(weather.get(q).getDateTimestamp()));
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setColor(getMyColor(q));
                    lineDataSets.add(lineDataSet);
                } else if (q == posDayTo) {
                    for (int w = 0; w < in; w++) {
                        if (weathers.get(q).size() > w) {
                            entries.add(new Entry(Float.valueOf(String.valueOf(weathers.get(q).get(w).getTemperature())),
                                    getEntryPos(MainSettings.unixTimeStampToDateTime(weathers.get(q).get(w).getDateTimestamp(), this))));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(entries, getDateLabel(weather.get(q).getDateTimestamp()));
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setColor(getMyColor(q));
                    lineDataSets.add(lineDataSet);
                } else {
                    for (int w = 0; w < 9; w++) {
                        if (weathers.get(q).size() > w) {
                            entries.add(new Entry(Float.valueOf(String.valueOf(weathers.get(q).get(w).getTemperature())),
                                    getEntryPos(MainSettings.unixTimeStampToDateTime(weathers.get(q).get(w).getDateTimestamp(), this))));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(entries, getDateLabel(weather.get(q).getDateTimestamp()));
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setColor(getMyColor(q));
                    lineDataSets.add(lineDataSet);
                }
            }
        }

            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < weathers.get(2).size(); i++) {
                values.add(i, MainSettings.unixTimeStampToDateTime(weathers.get(2).get(i).getDateTimestamp(), this));
            }
            String[] myValues = new String[values.size()];
            for (int i = 0; i < values.size(); i++) {
                myValues[i] = values.get(i);
            }

            lineChart.setData(new LineData(myValues, lineDataSets));

            lineChart.setVisibleXRangeMaximum(65f);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        if(posDayTo < posDayFrom){
            Snackbar.make(graph, "Next day do not may be lower then previous", Snackbar.LENGTH_LONG).show();
        }
        else if((posDayTo == posDayFrom) && (posTimeTo < posTimeFrom)){
            Snackbar.make(graph, "Next time do not may be lower then previous in current day", Snackbar.LENGTH_LONG).show();
        }
        else {
            temperatureGraph();
        }
    }

    public String getDateLabel(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public void spinnerDayFrom(){
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < weather.size(); i++){
            data.add(getDateLabel(weather.get(i).getDateTimestamp()));
        }
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerDayFrom);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Select day from");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posDayFrom = position;
                dataTimeFrom.clear();
                for(int i = 0; i < weathers.get(position).size(); i++){
                    dataTimeFrom.add(MainSettings.unixTimeStampToDateTime(weathers.get(position).get(i).getDateTimestamp(),getBaseContext()));
                }
                adapterTimeFrom.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void spinnerDayTo(){
        final ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < weather.size(); i++){
            data.add(getDateLabel(weather.get(i).getDateTimestamp()));
        }
        // адаптер
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerDayTo);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Select day to");
        // выделяем элемент
        spinner.setSelection(0);
        timeOfDayTo = 0;
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posDayTo = position;
                dataTimeTo.clear();
                for(int i = 0; i < weathers.get(position).size(); i++){
                    dataTimeTo.add(MainSettings.unixTimeStampToDateTime(weathers.get(position).get(i).getDateTimestamp(),getBaseContext()));
                }
                adapterTimeTo.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void spinnerTimeFrom(){
        for(int i = 0; i < weathers.get(0).size(); i++){
                dataTimeFrom.add(MainSettings.unixTimeStampToDateTime(weathers.get(timeOfDayFrom).get(i).getDateTimestamp(),this));
        }
        // адаптер
        adapterTimeFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataTimeFrom);
        adapterTimeFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerTimeFrom);
        spinner.setAdapter(adapterTimeFrom);
        // заголовок
        spinner.setPrompt("Select time from");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posTimeFrom = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void spinnerTimeTo(){
        for(int i = 0; i < weathers.get(0).size(); i++){
            dataTimeTo.add(MainSettings.unixTimeStampToDateTime(weathers.get(timeOfDayFrom).get(i).getDateTimestamp(),this));
        }
        // адаптер
        adapterTimeTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataTimeTo);
        adapterTimeTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerTimeTo);
        spinner.setAdapter(adapterTimeTo);
        // заголовок
        spinner.setPrompt("Select time to");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posTimeTo = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}