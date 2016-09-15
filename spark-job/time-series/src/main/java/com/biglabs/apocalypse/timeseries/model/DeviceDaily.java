package com.biglabs.apocalypse.timeseries.model;

import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by thainguy on 9/15/2016.
 */
public class DeviceDaily implements Serializable {
    private String house;
    private String device;
    private long year;
    private long month;
    private long day;
    private long aggcouner;
    private long value;

    public DeviceDaily(){}

    public DeviceDaily(DeviceDaily deviceDaily){
        this.house = deviceDaily.getHouse();
        this.device = deviceDaily.getDevice();
        this.year = deviceDaily.getYear();
        this.month = deviceDaily.getMonth();
        this.day = deviceDaily.getDay();
        this.value = deviceDaily.getValue();
        this.aggcouner = deviceDaily.getAggcouner();
    }

    public DeviceDaily(PowerRaw powerRaw){
        this.house = powerRaw.getHouse();
        this.device = powerRaw.getDevice();
        Calendar calendar = DateUtils.toCalendar(powerRaw.getTime());
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.value = powerRaw.getValue();
        this.aggcouner = 1;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
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
