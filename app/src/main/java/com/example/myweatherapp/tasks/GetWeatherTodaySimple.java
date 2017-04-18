package com.example.myweatherapp.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;

public abstract class GetWeatherTodaySimple extends AsyncTask<String, Void, String> {

    public GetWeatherTodaySimple(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    ProgressDialog progressDialog;

    Weather weather = new Weather();
    MainSettings mainSettings =  new MainSettings();

    public abstract void onSucess(Weather weather, String s);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!progressDialog.isShowing()) {
            progressDialog.setMessage("Update data");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        GetConnection http = new GetConnection();
        return http.getJsonObj(params);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        weather = mainSettings.parseTodayJson(s);
        onSucess(weather, s);
    }
}