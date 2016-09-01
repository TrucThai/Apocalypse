package com.biglabs.tool;

import com.biglabs.tool.model.House;
import com.biglabs.tool.model.Region;

import java.util.Map;

/**
 * Created by thainguy on 9/1/2016.
 */
public class HouseLoader extends LoaderBase<House>{
    private Map<String, Region> regions;

    public HouseLoader(){}

    public HouseLoader(Map<String, Region> regions){
        this.regions = regions;
    }

    @Override
    protected House create(String line) {
        String[] lineSplit = line.split(" ");
        House house = new House(lineSplit[0], lineSplit[1], lineSplit[2], lineSplit[3]);
        return house;
    }
}
