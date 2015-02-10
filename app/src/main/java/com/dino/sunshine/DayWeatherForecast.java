package com.dino.sunshine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DayWeatherForecast {

    private Date date;
    private String forecastDescription;
    private double minTemperature;
    private double maxTemperature;

    public DayWeatherForecast(Date date, String forecastDescription, double minTemperature, double maxTemperature) {
        this.date = date;
        this.forecastDescription = forecastDescription;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getForecastDescription() {
        return forecastDescription;
    }

    public void setForecastDescription(String forecastDescription) {
        this.forecastDescription = forecastDescription;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d");
        String dateString = dateFormat.format(date);

        return dateString + " - " + forecastDescription + " - " + minTemperature + "/" + maxTemperature;
    }
}
