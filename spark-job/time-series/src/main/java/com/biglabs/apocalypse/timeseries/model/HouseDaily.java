package com.biglabs.apocalypse.timeseries.model;

import java.io.Serializable;

/**
 * Created by thainguy on 9/14/2016.
 */
public class HouseDaily implements Serializable {
    private String house;
    private long year;
    private long month;
    private long day;
    private long aggcouner;
    private long value;

    public HouseDaily(){}

    public HouseDaily(PowerRaw powerRaw){
        this.house = powerRaw.getHouse();
        this.year = powerRaw.getTime().getYear();
        this.month = powerRaw.getTime().getMonth();
        this.day = powerRaw.getTime().getDay();
        this.value = powerRaw.getValue();
        this.aggcouner = 1;
    }

    public HouseDaily(HouseHourly houseHourly){
        this.house = houseHourly.getHouse();
        this.year = houseHourly.getYear();
        this.month = houseHourly.getMonth();
        this.day = houseHourly.getDay();
        this.value = houseHourly.getValue();
        this.aggcouner = 1;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
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
