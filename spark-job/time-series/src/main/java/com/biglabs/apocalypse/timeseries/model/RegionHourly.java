package com.biglabs.apocalypse.timeseries.model;

import java.io.Serializable;

/**
 * Created by thainguy on 9/14/2016.
 */
public class RegionHourly implements Serializable {
    private String region;
    private long year;
    private long month;
    private long day;
    private long hour;
    private long aggcouner;
    private long value;

    public RegionHourly(){}

    public RegionHourly(PowerRaw powerRaw){
        this.region = powerRaw.getRegion();
        this.year = powerRaw.getTime().getYear();
        this.month = powerRaw.getTime().getMonth();
        this.day = powerRaw.getTime().getDay();
        this.hour = powerRaw.getTime().getHours();
        this.value = powerRaw.getValue();
        this.aggcouner = 1;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getAggcouner() {
        return aggcouner;
    }

    public void setAggcouner(long aggcouner) {
        this.aggcouner = aggcouner;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
