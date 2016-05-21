package com.biglabs.apocalypse.timeseries;

import java.io.Serializable;

/**
 * Created by tail on 5/19/2016.
 */
public class RawWeatherData implements Serializable {
    String wsid;
    int year;
    int month;
    int day;
    int hour;
    double temperature;
    double dewpoint;
    double pressure;
    int windDirection;
    double windSpeed;
    int skyCondition;
    String skyConditionText;
    double oneHourPrecip;
    double sixHourPrecip;

    public RawWeatherData(String[] array) {
        wsid = array[0];
        year = Integer.parseInt(array[1]);
        month = Integer.parseInt(array[2]);
        day = Integer.parseInt(array[3]);
        hour = Integer.parseInt(array[4]);
        temperature = Double.parseDouble(array[5]);
        dewpoint = Double.parseDouble(array[6]);
        pressure = Double.parseDouble(array[7]);
        windDirection = Integer.parseInt(array[8]);
        windSpeed = Double.parseDouble(array[9]);
        skyCondition = Integer.parseInt(array[10]);
        skyConditionText = array[11];
        oneHourPrecip = Double.parseDouble(array[11]);
        if (array.length > 12) {
            sixHourPrecip = Double.parseDouble(array[12]);
        }
    }

    public String getWsid() {
        return wsid;
    }

    public void setWsid(String wsid) {
        this.wsid = wsid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getDewpoint() {
        return dewpoint;
    }

    public void setDewpoint(double dewpoint) {
        this.dewpoint = dewpoint;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getSkyCondition() {
        return skyCondition;
    }

    public void setSkyCondition(int skyCondition) {
        this.skyCondition = skyCondition;
    }

    public String getSkyConditionText() {
        return skyConditionText;
    }

    public void setSkyConditionText(String skyConditionText) {
        this.skyConditionText = skyConditionText;
    }

    public double getOneHourPrecip() {
        return oneHourPrecip;
    }

    public void setOneHourPrecip(double oneHourPrecip) {
        this.oneHourPrecip = oneHourPrecip;
    }

    public double getSixHourPrecip() {
        return sixHourPrecip;
    }

    public void setSixHourPrecip(double sixHourPrecip) {
        this.sixHourPrecip = sixHourPrecip;
    }
}
