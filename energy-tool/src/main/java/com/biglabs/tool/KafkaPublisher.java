package com.biglabs.tool;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by thainguy on 6/7/2016.
 */
public class KafkaPublisher implements DataPublisher {
    private Properties properties;
    private Producer<String, String> producer;
    private String topics;

    public KafkaPublisher(Properties properties, String topics){
        this.properties = properties;
        this.topics = topics;
        this.producer = new KafkaProducer<>(this.properties);
    }

    @Override
    public void send(String data) {
        producer.send(new ProducerRecord<String, String>(topics, data));
    }
}
