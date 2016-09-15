package com.biglabs.tool.model;

import com.biglabs.tool.DataPublisher;
import com.biglabs.tool.model.poco.Device;

/**
 * Created by thainguy on 9/6/2016.
 */
public class RTDevice extends Device{
    private DeviceSeed seed;
    private int dataCursor = -1;
    private long timeDiff = -1;
    private long timeUpperRange = 0;
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
        if(dataCursor == -1){
            dataCursor = 0;
            DeviceSeed.Seed seedData = seed.getData().get(dataCursor);
            timeDiff = time - seedData.getTime();
            DeviceSeed.Seed nextSeed = seed.getData().get(dataCursor+1);
            timeUpperRange = timeDiff + nextSeed.getTime();
        }

        updateSeedCursor(time);

        DeviceSeed.Seed seedData = seed.getData().get(dataCursor);
        String data = header + " " + System.currentTimeMillis()/1000 + " " + seedData.getData();
        publisher.send(data);
    }

    protected void updateSeedCursor(long time){
        while(timeUpperRange < time){
            dataCursor ++;
            if(dataCursor >= seed.getData().size() - 2){
                dataCursor = 0;
                DeviceSeed.Seed startSeed = seed.getData().get(dataCursor);
                timeDiff = time - startSeed.getTime();
                DeviceSeed.Seed nextSeed = seed.getData().get(dataCursor+1);
                timeUpperRange = timeDiff + nextSeed.getTime();
                break;
            }

            DeviceSeed.Seed seedData = seed.getData().get(dataCursor + 1);
            timeUpperRange = timeDiff + seedData.getTime();
        }
    }
}
