package com.biglabs.apocalypse.timeseries;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by thainguy on 7/22/2016.
 */
public class Time3Value implements Serializable {
    private Long time;
    private Long value1;
    private Long value2;
    private Long value3;
    private Date date;

    public Time3Value(long value1,long value2,long value3){
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.time = System.currentTimeMillis()/1000;
        this.date = new Date();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getValue1() {
        return value1;
    }

    public void setValue1(Long value1) {
        this.value1 = value1;
    }

    public Long getValue2() {
        return value2;
    }

    public void setValue2(Long value2) {
        this.value2 = value2;
    }

    public Long getValue3() {
        return value3;
    }

    public void setValue3(Long value3) {
        this.value3 = value3;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
