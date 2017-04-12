package com.example.myweatherapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myweatherapp.R;
import com.example.myweatherapp.adapter.RecycleForecastAdapter;
import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;

import java.util.ArrayList;

public class ExampleFragment extends Fragment {

    MainSettings mainSettings = new MainSettings();

    private View view;

    private RecyclerView recyclerView;

    ArrayList<Weather> weathers =  new ArrayList<>();

    private static final int LAYOUT = R.layout.fragment_recycler_view;

    public static ExampleFragment getInstance(){
        Bundle args = new Bundle();
        ExampleFragment fragment = new ExampleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetWeatherWeekForecast().execute(MainSettings.apiRequestWeek());
    }


    private class GetWeatherWeekForecast extends AsyncTask<String,Void,String> {

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
            weathers = mainSettings.groupByDay(mainSettings.parseLongForecastJson(s, getContext()));
            recyclerView.setAdapter(new RecycleForecastAdapter(weathers));
        }
    }
}