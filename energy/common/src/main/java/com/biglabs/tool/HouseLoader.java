package com.biglabs.tool;

import com.biglabs.tool.model.HouseDevice;
import com.biglabs.tool.model.RegionHouse;
import com.biglabs.tool.model.poco.House;
import com.biglabs.tool.model.poco.Region;
import com.biglabs.tool.model.HouseTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thainguy on 9/1/2016.
 */
public class HouseLoader{
    private Map<String, RegionHouse> regions;
    private Map<String, HouseTemplate> houseTemplates;

    public HouseLoader(){}

    public HouseLoader(Map<String, Region> regions, Map<String, HouseTemplate> houseTemplates){
        this.regions = convert(regions);
        this.houseTemplates = houseTemplates;
    }

    protected Map<String, RegionHouse> convert(Map<String, Region> regions){
        Map<String, RegionHouse> rhs = new HashMap<>();
        for (Region region: regions.values()) {
            RegionHouse rh = new RegionHouse(region);
            rhs.put(rh.getId(), rh);
        }
        return rhs;
    }

    public Map<String, RegionHouse> load(String root, String configFile) throws Exception{
        File file = new File(root, configFile);
        return load(file);
    }

    public Map<String, RegionHouse> load(String configFile) throws Exception {
        File file = new File(configFile);
        return load(file);
    }

    protected Map<String, RegionHouse> load(File file) throws Exception{
        if (!file.exists()) {
            throw new Exception(file.getName() + " doesn't exist");
        }
        if (file.isDirectory()) {
            throw new Exception(file.getName() + " is directory.");
        }

        Reader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (line != null) {
            String[] lineSplit = line.split(" ");
            String regionId = lineSplit[2];
            String templateName = lineSplit[1];
            String houseConf = lineSplit[0];
            ArrayList<String> houses = new ArrayList<>();
            if(houseConf.contains("-")){
                String[] houseRange = houseConf.split("-");
                int houseStart = Integer.parseInt(houseRange[0]);
                int houseEnd = Integer.parseInt(houseRange[1]);
                for(int i = houseStart;i <= houseEnd;i ++){
                    String house = "house" + i;
                    houses.add(house);
                }
            }else{
                String house = houseConf.startsWith("house") ? houseConf : "house" + houseConf;
                houses.add(house);
            }

            RegionHouse rh = regions.get(regionId);
            HouseTemplate template = houseTemplates.get(templateName);
            for(String houseName: houses){
                HouseDevice house = template.create(houseName);
                house.setRegionId(regionId);
                rh.getHouses().add(house);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return regions;
    }
}
