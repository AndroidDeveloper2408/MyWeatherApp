package com.example.myweatherapp.getMainSettings;

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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = Double.valueOf(temperature)-273.15;
    }

    public double getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(Double temperature) {
        this.min_temperature = temperature;
    }

    public void setMin_temperature(String min_temperature) {
        this.min_temperature = Double.valueOf(min_temperature)-273.15;
    }

    public double getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(Double temperature) {
        this.max_temperature = temperature;
    }

    public void setMax_temperature(String max_temperature) {
        this.max_temperature = Double.valueOf(max_temperature)-273.15;
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

    public void setSunrise(String dateString) {
        try {
            setSunrise(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                setSunrise(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setSunrise(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }

    public Date getSunset() {
        return sunset;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }

    public void setSunset(String dateString) {
        try {
            setSunset(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                setSunrise(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setSunset(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String dateString) {
        try {
            setDateTimestamp(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMMM", Locale.ENGLISH);
            try {
                setDateTimestamp(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setDateTimestamp(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time.substring(11,19);
    }

    public Date getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(Date dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    public void setDateTimestamp(String dateString) {
        try {
            setDateTimestamp(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMMM", Locale.ENGLISH);
            try {
                setDateTimestamp(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setDateTimestamp(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }
    private Date date;
    private Date dateTimestamp;
    private String city;
    private String country;
    private String time;
    private double temperature;
    private double min_temperature;
    private double max_temperature;
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