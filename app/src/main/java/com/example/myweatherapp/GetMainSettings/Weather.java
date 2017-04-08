package com.example.myweatherapp.GetMainSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Weather {

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String dateString) {
        try {
            setDate(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                setDate(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setDate(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public Double getWindDirectionDegree() {
        return windDirectionDegree;
    }

    public void setWindDirectionDegree(Double windDirectionDegree) {
        this.windDirectionDegree = windDirectionDegree;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public void setSunrise(Date sunrise) {
        this.sunrise = sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }

    private String city;
    private String country;
    private Date date;
    private String temperature;
    private String description;
    private String wind;
    private Double windDirectionDegree;
    private String pressure;
    private String humidity;
    private String rain;
    private String id;
    private String icon;
    private String lastUpdated;
    private Date sunrise;
    private Date sunset;
}