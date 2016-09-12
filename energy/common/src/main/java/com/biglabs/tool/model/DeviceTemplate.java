package com.biglabs.tool.model;

import com.biglabs.tool.model.poco.Device;

/**
 * Created by thainguy on 9/5/2016.
 */
public class DeviceTemplate {
    private DeviceSeed deviceSeed;
    private String channel;
    private String name;
    private String seedPath;

    public DeviceTemplate(){}

    public DeviceTemplate(String channel, String name, String seedPath){
        this.channel = channel;
        this.name = name;
        this.seedPath = seedPath;
        this.deviceSeed = new DeviceSeed("channel_" + channel + ".dat", seedPath);
    }

    public void init() throws Exception {
        deviceSeed.init();
    }

    public DeviceSeed getDeviceSeed() {
        return deviceSeed;
    }

    public Device create(String houseName){
        Device device = new Device(houseName + "-" + channel, name, "", houseName);
        return device;
    }

    public RTDevice createRT(String houseName){
        RTDevice device = new RTDevice(houseName + "-" + channel, name, "", houseName);
        device.setSeed(deviceSeed);
        return device;
    }
}
