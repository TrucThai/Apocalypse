package com.biglabs.tool.model;

import com.biglabs.tool.model.poco.House;
import com.biglabs.tool.model.poco.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RegionHouse extends Region {
    private List<HouseDevice> houses;

    public RegionHouse(Region region){
        super(region.getId(), region.getName(), region.getDesc());
        houses = new ArrayList<>();
    }

    public List<HouseDevice> getHouses() {
        return houses;
    }

    public void setHouses(List<HouseDevice> houses) {
        this.houses = houses;
    }
}
