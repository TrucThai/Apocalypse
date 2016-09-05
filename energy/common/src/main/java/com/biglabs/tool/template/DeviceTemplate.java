package com.biglabs.tool.template;

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
        this.deviceSeed = new DeviceSeed("chanel_" + channel + ".dat", seedPath);
    }

    public void init() throws Exception {
        deviceSeed.init();
    }

    public DeviceSeed getDeviceSeed() {
        return deviceSeed;
    }
}
