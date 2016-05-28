package com.biglabs.apocalypse.timeseries;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Created by tail on 5/25/2016.
 */
public class DailyPrecipitation  implements Serializable {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public double getOneHourPrecip() {
        return oneHourPrecip;
    }

    public void setOneHourPrecip(double oneHourPrecip) {
        this.oneHourPrecip = oneHourPrecip;
    }

    private String key;
    private String wsid;
    private int year;
    private int month;
    private int day;
    private double oneHourPrecip;

    public DailyPrecipitation(String wsid, int year, int month, int day, double oneHourPrecip) {
        this.wsid = wsid;
        this.year = year;
        this.month = month;
        this.day = day;
        this.oneHourPrecip = oneHourPrecip;
        this.key = MessageFormat.format("{0}_{1}{2}{3}", wsid, year, month, day);
    }
}
