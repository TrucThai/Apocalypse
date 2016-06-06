package com.biglabs.apocalypse.timeseries;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hung on 6/6/2016.
 */
public class HourlyPowerData {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private String hourKey;
    private String home;
    private String device;
    private Date time;
    private long value;

    public HourlyPowerData(String home, String device, Date time, long value) {
        this.home = home;
        this.device = device;
        this.time = time;
        this.value = value;
        this.hourKey = DATE_FORMAT.format(time);
    }

    public HourlyPowerData() {
    }

    public HourlyPowerData(RawPowerData raw) {
        this(raw.getHome(), raw.getDevice(), raw.getTime(), raw.getValue());
    }


    public String getHourKey() {
        return hourKey;
    }

    public void setHourKey(String hourKey) {
        this.hourKey = hourKey;
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
