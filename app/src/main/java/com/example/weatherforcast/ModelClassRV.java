package com.example.weatherforcast;

public class ModelClassRV {
    private String temperature, time, iconURL, windSpeed;

    public ModelClassRV(String temperature, String time, String iconURL, String windSpeed) {
        this.temperature = temperature;
        this.time = time;
        this.iconURL = iconURL;
        this.windSpeed = windSpeed;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
