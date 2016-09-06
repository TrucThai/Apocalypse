package com.biglabs.tool;

import com.biglabs.tool.model.HouseTemplate;
import com.biglabs.tool.model.RTHouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RTHouseLoader {
    private Map<String, HouseTemplate> houseTemplates;

    public RTHouseLoader(){}

    public RTHouseLoader(Map<String, HouseTemplate> houseTemplates){
        this.houseTemplates = houseTemplates;
    }

    public Map<String, RTHouse> load(String root, String configFile) throws  Exception{
        File file = new File(root, configFile);
        return load(file);
    }

    public Map<String, RTHouse> load(String configFile) throws Exception {
        File file = new File(configFile);
        return load(file);
    }

    protected Map<String, RTHouse> load(File file) throws Exception{
        if (!file.exists()) {
            throw new Exception(file.getName() + " doesn't exist");
        }
        if (file.isDirectory()) {
            throw new Exception(file.getName() + " is directory.");
        }

        Map<String, RTHouse> houseMap = new HashMap<>();
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

            HouseTemplate template = houseTemplates.get(templateName);
            for(String houseName: houses){
                RTHouse house = template.createRT(houseName);
                house.setRegionId(regionId);
                houseMap.put(house.getId(), house);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return houseMap;
    }
}
