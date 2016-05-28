package com.biglabs;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: data-tool <brokers> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <topics> is a list of one or more kafka topics to publish to\n\n");
            System.exit(1);
        }

        String brokers = args[0];
        String topics = args[1];
        String dataRoot = args[2];

        System.out.println("broker " + brokers);
        System.out.println("topic " + topics);
        System.out.println("dataroot " + dataRoot);

        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
//        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        ArrayList<File> files = new ArrayList<File>();
        listFiles(dataRoot, files);

        if(files.size() == 0){
            System.err.println("The data directory doesn't contain any file");
            System.exit(2);
        }

        long startTime = System.currentTimeMillis();
        long numberMsg = 0;
        for(File file: files){
            try{
                if(file.getName().startsWith("channel")) {
                    System.out.println("processing: " + file.getAbsolutePath());
                    String parent = file.getParentFile().getName();
                    String name = file.getName();
                    String header = parent + " " + name + " ";
                    Reader reader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line = bufferedReader.readLine();

                    while (line != null) {
                        numberMsg++;
                        producer.send(new ProducerRecord<String, String>(topics, line, header + line));
                        line = bufferedReader.readLine();
                    }

                    bufferedReader.close();
                }
            } catch (Exception ex){
                System.err.println(ex.toString());
            }
        }

        producer.flush();

        long endTime = System.currentTimeMillis();
        System.out.println("total: " + numberMsg);
        System.out.println("Total time: " + (endTime - startTime));
        producer.close();
    }

    public static void listFiles(String dir, ArrayList<File> files){
        File dirFile = new File(dir);
        if(dirFile.isDirectory()){
            File[] fileList = dirFile.listFiles();
            for(File file: fileList){
                if(file.isFile()){
                    files.add(file);
                } else if(file.isDirectory()){
                    listFiles(file.getAbsolutePath(), files);
                }
            }
        } else if(dirFile.isFile()){
            files.add(dirFile);
        }

    }
}
