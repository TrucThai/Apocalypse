package com.biglabs.tool;

import com.biglabs.tool.model.HouseTemplate;
import com.biglabs.tool.model.RegionHouse;
import com.biglabs.tool.model.poco.Region;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

/**
 * Created by thainguy on 9/1/2016.
 */
public class App {
    public static void main( String[] args ) throws Exception {
        String configRoot = args[0];
        String jsonFile = args[1];

        RegionLoader regionLoader = new RegionLoader();
        Map<String, Region> regions = regionLoader.load(configRoot, "regions.conf");

        HouseTemplateLoader htl = new HouseTemplateLoader();
        Map<String, HouseTemplate> houseTemplateMap = htl.load(configRoot, "housetemplates.conf");

        HouseLoader houseLoader = new HouseLoader();
        Map<String, RegionHouse> regionHouseMap = houseLoader.load(configRoot, "houses.conf");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(jsonFile), regionHouseMap.values());
    }
}
