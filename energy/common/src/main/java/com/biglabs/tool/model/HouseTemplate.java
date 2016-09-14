package com.biglabs.tool.model;


import com.biglabs.tool.model.poco.House;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by thainguy on 9/1/2016.
 */
public class HouseTemplate extends House{
    private ArrayList<DeviceTemplate> deviceTemplates;
    private String name;
    private String deviceConfigFile;
    private String seedDir;

    public HouseTemplate(){}

    public HouseTemplate(String conf){
        String[] splits = conf.split(" ");

        this.id = this.name = splits[0];
        this.deviceConfigFile = splits[1];
        this.seedDir = splits[2];
    }

    public void init() throws  Exception{
        File file = new File(deviceConfigFile);
        Reader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        deviceTemplates = new ArrayList<>();
        while (line != null) {
            System.out.println("Process line: " + line);
            String[] split = line.split(" ");
            DeviceTemplate deviceTemplate = new DeviceTemplate(split[0], split[1], seedDir);
            deviceTemplate.init();
            deviceTemplates.add(deviceTemplate);
            line = bufferedReader.readLine();
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<DeviceTemplate> getDeviceTemplates() {
        return deviceTemplates;
    }

    public HouseDevice create(String name, String regionId){
        HouseDevice house = new HouseDevice(name, name, "", regionId);
        for(DeviceTemplate dt: deviceTemplates){
            house.getDevices().add(dt.create(house.getName()));
        }
        return  house;
    }

    public RTHouse createRT(String name, String regionId){
        RTHouse house = new RTHouse(name, name, "", regionId);
        for(DeviceTemplate dt: deviceTemplates){
            house.getDevices().add(dt.createRT(house.getName(), house.getRegionId()));
        }
        return  house;
    }
}
