package com.biglabs.apocalypse.timeseries.model;

import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by thainguy on 9/14/2016.
 */
public class RegionDaily implements Serializable {
    private String region;
    private long year;
    private long month;
    private long day;
    private long aggcouner;
    private long value;

    public RegionDaily(){}

    public RegionDaily(RegionDaily regionDaily){
        this.region = regionDaily.getRegion();
        this.year = regionDaily.getYear();
        this.month = regionDaily.getMonth();
        this.day = regionDaily.getDay();
        this.value = regionDaily.getValue();
        this.aggcouner = regionDaily.getAggcouner();
    }

    public RegionDaily(PowerRaw powerRaw){
        this.region = powerRaw.getRegion();
        Calendar calendar = DateUtils.toCalendar(powerRaw.getTime());
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.value = powerRaw.getValue();
        this.aggcouner = 1;
    }

    public RegionDaily(RegionHourly regionHourly){
        this.region = regionHourly.getRegion();
        this.year = regionHourly.getYear();
        this.month = regionHourly.getMonth();
        this.day = regionHourly.getDay();
        this.value = regionHourly.getValue();
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
