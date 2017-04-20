package com.example.myweatherapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweatherapp.R;
import com.example.myweatherapp.adapters.RecycleForecastAdapter;
import com.example.myweatherapp.adapters.TabsPagerFragmentAdapter;
import com.example.myweatherapp.getMainSettings.MainSettings;
import com.example.myweatherapp.getMainSettings.Weather;
import com.example.myweatherapp.tasks.GetWeatherTodaySimple;
import com.example.myweatherapp.tasks.GetWeatherWeekForecast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.myweatherapp.getMainSettings.MainSettings.unixTimeStampToDateTime;

public class MainWeatherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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

    SignInButton signInButton;

    View appView;

    String key_facebook = "pISoKJA2jI8dIDY/f0MMIl0txSw=";

    private static final int RC_SIGN_IN = 9001;

    private ViewPager viewPager;

    LoginButton loginButton;
    Button signOut;

    ProgressDialog progressDialog;

    CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;

    public GoogleApiClient mGoogleApiClientPlaces;

    int PLACE_PICKER_REQUEST = 1;

    NavigationView navigationView;
    ImageView img;
    TextView temp, city, descr;

    private boolean isData = false;

    Weather todayweather = new Weather();
    MainSettings mainSettings = new MainSettings();

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        appView = findViewById(R.id.viewApp);
        progressDialog = new ProgressDialog(this);

        initTabs();
        initViews();

        facebookInit();

        googlePlusInit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("isdata", false)) {
            todayweather = mainSettings.parseTodayJson(prefs.getString("lastToday", "123"));
            setValuesToViews(todayweather);
        }

        mGoogleApiClientPlaces = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, 1, this)
                .build();

    }

    public void googlePlusInit(){
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOut = (Button) findViewById(R.id.btnSignOut);

        signInButton.setOnClickListener(this);
        signOut.setOnClickListener(this);
        signOut.setVisibility(View.GONE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(MainWeatherActivity.this.getResources().getString(R.string.server_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String name1 = acct.getDisplayName();
            String email1 = acct.getEmail();
            String img_url = acct.getPhotoUrl().toString();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            signOut.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);

        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        else if(requestCode == RC_SIGN_IN) {
           GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Toast.makeText(this, "Google Login Details:" + googleSignInResult.getStatus().toString(), Toast.LENGTH_LONG).show();
            handleSignInResult(googleSignInResult);
        }
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng location = place.getLatLng();
                String lat = String.valueOf(location.latitude);
                String lon = String.valueOf(location.longitude);
                new GetTodayWeather(progressDialog, this).execute(MainSettings.apiRequestToday(lat, lon));
                new GetWeekWeather(this).execute(MainSettings.apiRequestWeek(lat, lon));
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putString("lat", lat);
                editor.putString("lon", lon);
                editor.commit();
            }
        }
    }

    public void facebookInit() {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Toast.makeText(getApplicationContext(), "1 " + profile.getFirstName(), Toast.LENGTH_SHORT).show();
                            mProfileTracker.stopTracking();
                        }
                    };
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    Toast.makeText(getApplicationContext(), "2 " + profile.getFirstName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "facebook - onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("facebook - onError", e.getMessage());
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
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
        tvLastUpdate = (TextView) findViewById(R.id.tvLastUpdate);

        temp = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header1);
        city = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header2);
        descr = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header3);
        img = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        if (id == R.id.action_refresh) {
            if (isNetworkAvailable()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String lat = prefs.getString("lat", "47.8167");
            String lon = prefs.getString("lon", "35.1833");
            new GetTodayWeather(progressDialog, this).execute(MainSettings.apiRequestToday(lat, lon));
            new GetWeekWeather(this).execute(MainSettings.apiRequestWeek(lat, lon));
            isData = true;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isdata", isData);
            editor.commit();
            }
            else{
                Snackbar.make(appView, "Connection is not available", Snackbar.LENGTH_LONG).show();
            }
        }
        else if(id == R.id.action_graph){
            Intent intent = new Intent(this, GraphActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_choicePlace) {
            if (isNetworkAvailable()) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
            else{
                Snackbar.make(appView, "Connection is not available", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            if (isNetworkAvailable()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            }
            else{
                Snackbar.make(appView, "Connection is not available", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_send) {
            if (isNetworkAvailable()) {
                signIn();;
            }
            else{
                Snackbar.make(appView, "Connection is not available", Snackbar.LENGTH_LONG).show();
            }
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


    public void setValuesToViews(Weather todayWeather) {
        temp.setText(String.format("%.1f °C", todayWeather.getTemperature()));
        city.setText(todayWeather.getCity());
        descr.setText(todayWeather.getDescription());
        Picasso.with(MainWeatherActivity.this)
                .load(MainSettings.getImage(todayWeather.getIcon()))
                .into(img);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tvLastUpdate.setText(prefs.getString("lastUpdate", "No Data"));
    }

    public void saveDataToday(String json){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString("lastToday", json);
        editor.putString("lastUpdate", String.format("Last Updated: %s", MainSettings.getDateNow()));
        editor.commit();
    }

    public void saveDataWeek(String json){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString("lastWeek", json);
        editor.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class GetTodayWeather extends GetWeatherTodaySimple {

        public GetTodayWeather(ProgressDialog progressDialog, Context context) {
            super(progressDialog, context);
        }

        @Override
        public void onSucess(Weather weather, String s) {
            saveDataToday(s);
            setValuesToViews(weather);
        }
    }

    private class GetWeekWeather extends GetWeatherWeekForecast{

        public GetWeekWeather(Context context) {
            super(context);
        }

        @Override
        public void onSucess(ArrayList<Weather> weatherArrayList, String s) {
            saveDataWeek(s);
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
            recyclerView.setAdapter(new RecycleForecastAdapter(weatherArrayList));
        }
    }
}

/*


     private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

  @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void googleInit(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        }
*/