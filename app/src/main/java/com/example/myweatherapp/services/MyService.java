package com.example.myweatherapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.myweatherapp.R;
import com.example.myweatherapp.activities.MainWeatherActivity;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;
import com.example.myweatherapp.tasks.GetWeatherTodaySimple;

import static com.example.myweatherapp.getMainSettings.MainSettings.setIconToList;

public class MyService extends Service {
    public MyService() {
    }

    NotificationManager nm;

    SharedPreferences prefs;
    ProgressDialog progressDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        progressDialog = new ProgressDialog(this);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
                String lat = prefs.getString("lat", "47.8167");
                String lon = prefs.getString("lon", "35.1833");
                new GetTodayWeather(null, getApplicationContext()).execute(MainSettings.apiRequestToday(lat, lon));
        return super.onStartCommand(intent, flags, startId);
    }

    public void setViews(Weather weather, String json){
        if(json.contains("Error: Not found city")){
            Toast.makeText(this, "Error: Not found city", Toast.LENGTH_LONG).show();
            sendNotif("Error", "Not found city", R.drawable.cast_ic_notification_1, R.drawable.cast_ic_notification_2);
        }
        else {
            sendNotif("Today: " + String.format("%.1f °C", weather.getTemperature()), weather.getDescription(),
                    setIconToList(weather.getIcon()),setIconToList(weather.getIcon()));
        }
    }

    void sendNotif(String myTitle, String myText, int smallIcon, int largeIcon) {
        Notification.Builder nBuilder = new Notification.Builder(this)
                .setContentTitle(myTitle)
                .setContentText(myText)
                .setAutoCancel(false)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), largeIcon));

        // 3-я часть  Click on notification
        Intent intent = new Intent(this, MainWeatherActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        nBuilder.setContentIntent(pIntent);

        Notification notif = nBuilder.build();
        // ставим флаг, чтобы уведомление пропало после нажатия
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.defaults = Notification.DEFAULT_ALL;

        // отправляем
        nm.notify(1, notif);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class GetTodayWeather extends GetWeatherTodaySimple {

        public GetTodayWeather(ProgressDialog progressDialog, Context context) {
            super(progressDialog, context);
        }

        @Override
        public void onSucess(Weather weather, String s) {
            setViews(weather, s);
        }
    }
}