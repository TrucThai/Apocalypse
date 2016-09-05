package com.biglabs.tool.template;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by thainguy on 9/1/2016.
 */
public class HouseTemplate {
    private ArrayList<DeviceTemplate> deviceTemplates;
    private String name;
    private String deviceConfigFile;
    private String seedDir;

    public HouseTemplate(){}

    public HouseTemplate(String conf){
        String[] splits = conf.split(" ");
        this.name = splits[0];
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
            String[] split = line.split(" ");
            DeviceTemplate deviceTemplate = new DeviceTemplate(split[0], split[1], seedDir);
            deviceTemplate.init();
            deviceTemplates.add(deviceTemplate);
        }
    }

    public String getName() {
        return name;
    }
}
