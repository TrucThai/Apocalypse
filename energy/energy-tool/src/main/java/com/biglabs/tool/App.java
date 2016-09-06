package com.biglabs.tool;

import com.biglabs.tool.model.HouseTemplate;
import com.biglabs.tool.model.RTHouse;

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
        String configRoot = args[0];
        String brokers = args[1];
        String topics = args[2];

        System.out.println("configRoot " + configRoot);
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

        HouseTemplateLoader htl = new HouseTemplateLoader();
        Map<String, HouseTemplate> houseTemplateMap = htl.load(configRoot, "housetemplates.conf");
        RTHouseLoader rtHouseLoader = new RTHouseLoader(houseTemplateMap);
        Map<String, RTHouse> houses = rtHouseLoader.load(configRoot, "houses.conf");

        DataPublisher publisher = new KafkaPublisher(props, topics);

        long numDevices = 0;
        for (RTHouse house: houses.values()) {
            numDevices += house.getDevices().size();
        }
        System.out.println("Number of device " + numDevices);
        System.out.println("Begin sending data");

        long counter = 0;
        while(true){
            for (RTHouse house: houses.values()) {
                try {
                    house.run(publisher);
                } catch (Exception ex){
                    System.err.println(ex);
                }
            }
            counter ++;
            if(counter % 60 == 0){
                System.out.println((new Date()) + " total sent messages " + counter * numDevices);
            }
            Thread.sleep(900);
        }
    }

}
