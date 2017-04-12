package com.example.myweatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myweatherapp.adapter.TabsPagerFragmentAdapter;
import com.example.myweatherapp.getMainSettings.GetConnection;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.myweatherapp.getMainSettings.MainSettings.unixTimeStampToDateTime;

public class MainWeatherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView tvCity;
    TextView tvTemperature;
    TextView tvDescription;
    TextView tvWind;
    TextView tvPressure;
    TextView tvHumidity;
    TextView tvSunrise;
    TextView tvSunset;
    TextView tvLastUpdate;

    Weather todayWeather;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        initTabs();
        initViews();

        new GetWeatherTodaySimple().execute(MainSettings.apiRequestToday());
    }

    public void initViews() {
        imageView = (ImageView) findViewById(R.id.imageView3);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvWind = (TextView) findViewById(R.id.tvWind);
        tvPressure = (TextView) findViewById(R.id.tvHumidity);
        tvHumidity = (TextView) findViewById(R.id.tvPressure);
        tvSunrise = (TextView) findViewById(R.id.tvSunrise);
        tvSunset = (TextView) findViewById(R.id.tvSunset);
        tvLastUpdate = (TextView)findViewById(R.id.tvLastUpdate);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter tabsPagerFragmentAdapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerFragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private String parseTodayJson(String result) {
        try {
            todayWeather = new Weather();
            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                return "CITY_NOT_FOUND";
            }

            String city = reader.getString("name");
            String country = "";
            JSONObject countryObj = reader.optJSONObject("sys");
            if (countryObj != null) {
                country = countryObj.getString("country");
                todayWeather.setSunrise(countryObj.getString("sunrise"));
                todayWeather.setSunset(countryObj.getString("sunset"));
            }
            todayWeather.setCity(city);
            todayWeather.setCountry(country);

            /*JSONObject coordinates = reader.getJSONObject("coord");
            if (coordinates != null) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putFloat("latitude", (float) coordinates.getDouble("lon")).putFloat("longitude", (float) coordinates.getDouble("lat")).commit();
            }*/

            JSONObject main = reader.getJSONObject("main");

            todayWeather.setTemperature(main.getString("temp"));
            todayWeather.setDescription(reader.getJSONArray("weather").getJSONObject(0).getString("description"));
            JSONObject windObj = reader.getJSONObject("wind");
            todayWeather.setWind(windObj.getString("speed"));
            if (windObj.has("deg")) {
                todayWeather.setWindDirectionDegree(windObj.getDouble("deg"));
            } else {
                Log.e("parseTodayJson", "No wind direction available");
                todayWeather.setWindDirectionDegree(null);
            }
            todayWeather.setPressure(main.getString("pressure"));
            todayWeather.setHumidity(main.getString("humidity"));

            final String idString = reader.getJSONArray("weather").getJSONObject(0).getString("id");
            todayWeather.setId(idString);
            todayWeather.setIcon(reader.optJSONArray("weather").getJSONObject(0).getString("icon"));

            /*SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("lastToday", result);
            editor.commit();*/

        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            return "JSON_EXCEPTION";
        }

        return "CITY FOUND SUCESS";
    }

    public void setValuesToViews() {
        Picasso.with(MainWeatherActivity.this)
                .load(MainSettings.getImage(todayWeather.getIcon()))
                .into(imageView);
        tvCity.setText(todayWeather.getCity());
        tvTemperature.setText(String.format("%.1f °C", todayWeather.getTemperature()));
        tvDescription.setText(todayWeather.getDescription());
        tvWind.setText("Wind: " + todayWeather.getWind() + "m/s");
        tvPressure.setText("Pressure: " + todayWeather.getPressure() + "hPa");
        tvHumidity.setText("Humidity: " + todayWeather.getHumidity() + "%");
        tvSunrise.setText("Sunrise" + ": " + unixTimeStampToDateTime(todayWeather.getSunrise(), this));
        tvSunset.setText("Sunset" + ": " + unixTimeStampToDateTime(todayWeather.getSunset(), this));
        tvLastUpdate.setText(String.format("Last Updated: %s", MainSettings.getDateNow()));
    }

    private class GetWeatherTodaySimple extends AsyncTask<String, Void, String> {

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
            parseTodayJson(s);
            setValuesToViews();
        }
    }
}