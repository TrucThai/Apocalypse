package com.biglabs.apocalypse.timeseries.model;

/**
 * Created by thainguy on 9/14/2016.
 */
public class ModelHelper {
    public static RegionDaily combine(RegionDaily r1, RegionDaily r2){
        RegionDaily regionDaily = new RegionDaily(r1);
        regionDaily.setValue(r1.getValue() + r2.getValue());
        regionDaily.setAggcouner(r1.getAggcouner() + r2.getAggcouner());
        return regionDaily;
    }

    public static RegionHourly combine(RegionHourly r1, RegionHourly r2){
        RegionHourly regionHourly = new RegionHourly(r1);
        regionHourly.setValue(r1.getValue() + r2.getValue());
        regionHourly.setAggcouner(r1.getAggcouner() + r2.getAggcouner());
        return regionHourly;
    }

    public static HouseDaily combine(HouseDaily r1, HouseDaily r2){
        HouseDaily houseDaily = new HouseDaily(r1);
        houseDaily.setValue(r1.getValue() + r2.getValue());
        houseDaily.setAggcouner(r1.getAggcouner() + r2.getAggcouner());
        return houseDaily;
    }

    public static HouseHourly combine(HouseHourly r1, HouseHourly r2){
        HouseHourly houseHourly = new HouseHourly(r1);
        houseHourly.setValue(r1.getValue() + r2.getValue());
        houseHourly.setAggcouner(r1.getAggcouner() + r2.getAggcouner());
        return houseHourly;
    }
}
