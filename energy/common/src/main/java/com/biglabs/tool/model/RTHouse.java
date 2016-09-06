package com.biglabs.tool.model;

import com.biglabs.tool.DataPublisher;
import com.biglabs.tool.model.poco.House;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RTHouse extends House{
    private List<RTDevice> devices;

    public RTHouse(String id, String name, String desc, String regionId){
        super(id, name, desc, regionId);
        devices = new ArrayList<>();
    }

    public List<RTDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<RTDevice> devices) {
        this.devices = devices;
    }

    public void run(DataPublisher publisher){
        long time = System.currentTimeMillis()/1000;
        for (RTDevice device: devices) {
            device.run(publisher, time);
        }
    }
}
