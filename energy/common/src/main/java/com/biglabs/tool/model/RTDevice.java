package com.biglabs.tool.model;

import com.biglabs.tool.model.poco.Device;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RTDevice extends Device{
    private DeviceSeed seed;

    public DeviceSeed getSeed() {
        return seed;
    }

    public void setSeed(DeviceSeed seed) {
        this.seed = seed;
    }

    public RTDevice(String id, String name, String desc, String houseId){
        super(id, name, desc, houseId);
    }
}
