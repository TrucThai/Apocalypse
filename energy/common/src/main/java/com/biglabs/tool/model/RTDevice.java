package com.biglabs.tool.model;

import com.biglabs.tool.DataPublisher;
import com.biglabs.tool.model.poco.Device;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RTDevice extends Device{
    private DeviceSeed seed;
    private int dataCursor = 0;
    private long timeDiff = -1;
    private String header;

    public DeviceSeed getSeed() {
        return seed;
    }

    public void setSeed(DeviceSeed seed) {
        this.seed = seed;
    }

    public RTDevice(String id, String name, String desc, String houseId, String zoneId){
        super(id, name, desc, houseId);
        header = zoneId + " " + houseId + " " + id;
    }

    public void run(DataPublisher publisher, long time){
        if(dataCursor == 0){
            DeviceSeed.Seed seedData = seed.getData().get(dataCursor);
            timeDiff = time - seedData.getTime();
        }

        updateSeedCursor(time);

        DeviceSeed.Seed seedData = seed.getData().get(dataCursor);
        String data = header + " " + System.currentTimeMillis()/1000 + " " + seedData.getData();
        publisher.send(data);
    }

    protected void updateSeedCursor(long time){
        DeviceSeed.Seed seedData = seed.getData().get(dataCursor);
        while(seedData.getTime() + timeDiff < time){
            dataCursor ++;
            if(dataCursor >= seed.getData().size()){
                dataCursor = 0;
                DeviceSeed.Seed startSeed = seed.getData().get(dataCursor);
                timeDiff = time - startSeed.getTime();
                break;
            }

            seedData = seed.getData().get(dataCursor);
        }
    }
}
