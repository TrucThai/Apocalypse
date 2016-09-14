package com.biglabs.apocalypse.timeseries.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by thainguy on 9/14/2016.
 */
public class PowerRaw implements Serializable{
    private String region;
    private String house;
    private String device;
    private Date time;
    private long value;

    public PowerRaw(String region, String house, String device, Date time, long value) {
        this.region = region;
        this.house = house;
        this.device = device;
        this.time = time;
        this.value = value;
    }

    public PowerRaw() {
    }

    public PowerRaw(String[] array) {
        this(array[0], array[1], array[2], new Date(Long.parseLong(array[3]) * 1000), Long.parseLong(array[4]));
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
