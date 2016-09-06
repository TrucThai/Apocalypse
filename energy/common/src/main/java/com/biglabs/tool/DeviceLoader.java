package com.biglabs.tool;

import com.biglabs.tool.model.poco.Device;

/**
 * Created by thainguy on 9/5/2016.
 */
public class DeviceLoader  extends LoaderBase<Device>{

    @Override
    protected Device create(String line) {
        String[] lineSplit = line.split(" ");
        Device device = new Device(lineSplit[0], lineSplit[1], "", "");
        return device;
    }
}
