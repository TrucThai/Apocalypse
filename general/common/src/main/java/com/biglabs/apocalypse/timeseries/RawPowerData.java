package com.biglabs.apocalypse.timeseries;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tail on 5/28/2016.
 */
public class RawPowerData implements Serializable {
    private String home;
    private String device;
    private Date time;
    private long value;

    public RawPowerData(String home, String device, Date time, long value) {
        this.home = home;
        this.device = device;
        this.time = time;
        this.value = value;
    }

    public RawPowerData() {
    }

    public RawPowerData(String[] array) {
        this(array[0], array[1], new Date(Long.parseLong(array[2]) * 1000), Long.parseLong(array[3]));
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
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
