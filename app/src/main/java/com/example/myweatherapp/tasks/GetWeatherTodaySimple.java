package com.example.myweatherapp.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;

public abstract class GetWeatherTodaySimple extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;

    Weather weather = new Weather();
    MainSettings mainSettings =  new MainSettings();
    Context context;


    public GetWeatherTodaySimple(ProgressDialog progressDialog, Context context) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

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
        if(s.contains("Error: Not found city")){
            progressDialog.dismiss();
            Toast.makeText(context, "Error: Not found city", Toast.LENGTH_LONG).show();
            return;
        }
        weather = mainSettings.parseTodayJson(s);
        progressDialog.dismiss();
        onSucess(weather, s);
    }
}