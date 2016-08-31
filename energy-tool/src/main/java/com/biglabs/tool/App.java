package com.biglabs.tool;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        String seedRoot = args[0];
        String deviceConf = args[1];
        String brokers = args[2];
        String topics = args[3];

        System.out.println("seedRoot " + seedRoot);
        System.out.println("deviceConf " + deviceConf);
        System.out.println("brokers " + brokers);
        System.out.println("topics " + topics);

        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        DataPublisher publisher = new KafkaPublisher(props, topics);
        Map<String, DataSeed> seeds = Util.load(seedRoot);
        List<Device> devices = Util.loadDevices(deviceConf, seeds, publisher);

        System.out.println("Number of device " + devices.size());
        System.out.println("Begin sending data");

        long counter = 0;
        while(true){
            for(Device device: devices){
                try {
                    device.run();
                } catch (Exception ex){
                    System.err.println(ex);
                }
            }
            counter ++;
            if(counter % 60 == 0){
                System.out.println((new Date()) + " total sent messages " + counter * devices.size());
            }
            Thread.sleep(900);
        }
    }
}
