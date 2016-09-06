package com.biglabs.tool.model;

import com.biglabs.tool.model.poco.Device;
import com.biglabs.tool.model.poco.House;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thainguy on 9/6/2016.
 */
public class HouseDevice extends House {
    private List<Device> devices;

    public HouseDevice(String id, String name, String desc, String regionId){
        super(id, name, desc, regionId);
        devices = new ArrayList<>();
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
