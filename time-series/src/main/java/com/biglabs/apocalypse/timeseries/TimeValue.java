package com.biglabs.apocalypse.timeseries;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by thainguy on 7/22/2016.
 */
public class TimeValue implements Serializable {
    private Long time;
    private Long value;
    private Date date;

    public TimeValue(long value){
        this.value = value;
        this.time = System.currentTimeMillis()/1000;
        this.date = new Date();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
