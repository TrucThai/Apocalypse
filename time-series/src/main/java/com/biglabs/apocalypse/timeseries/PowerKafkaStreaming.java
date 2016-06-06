package com.biglabs.apocalypse.timeseries;

import com.datastax.spark.connector.embedded.EmbeddedKafka;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.writer.RowWriterFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kafka.serializer.StringDecoder;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;
import scala.Tuple5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.saveToEs;

/**
 * Created by tail on 5/28/2016.
 */
public class PowerKafkaStreaming {
    int sparkCleanerTtl = 3600 * 2;

    public void run(String[] args) {
        //System.out.println(scala.tools.nsc.Properties.versionString());
        //printClassPath();

        ClassLoader classLoader = getClass().getClassLoader();

        try {
            System.out.println(org.apache.commons.io.IOUtils.toString(classLoader.getResourceAsStream("apocalypse.conf")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Config rootConf = ConfigFactory.parseResources(classLoader, "apocalypse.conf");
        System.out.print(rootConf.entrySet());
        Config kafka = rootConf.getConfig("kafka");
        String KafkaTopicRaw = kafka.getString("topic.power");
        Config apocalypse = rootConf.getConfig("apocalypse");
        String CassandraKeyspace = apocalypse.getString("cassandra.power.keyspace");
        String CassandraTableRaw = apocalypse.getString("cassandra.power.table.raw");
        String HourlyCassandraTable = apocalypse.getString("cassandra.power.table.hourly_power_data");

        Config spark = rootConf.getConfig("spark");
        String sparkMaster = spark.getString("master");// "local[*]";
        Config cassandra = rootConf.getConfig("cassandra");
        String cassandraHosts = cassandra.getString("connection.host");//"localhost";

        SparkConf conf = new SparkConf()
                .setAppName(PowerKafkaStreaming.class.getSimpleName())
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHosts)
                .set("spark.cleaner.ttl", String.valueOf(sparkCleanerTtl));

        // es

        conf.set("es.index.auto.create", "true");

        // JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.milliseconds(SparkStreamingBatchInterval));
        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(2000));

        String brokers;
        // try  {
        brokers = kafka.getString("hosts");
        // } catch (Exception ex) {
        // /** Starts the Kafka broker and Zookeeper. */
        // EmbeddedKafka embeddedKafka = new EmbeddedKafka();

        // /** Creates the raw data topic. */
        // embeddedKafka.createTopic(KafkaTopicRaw, 1, 1);
        // brokers = embeddedKafka.kafkaConfig().hostName() + ":" + embeddedKafka.kafkaConfig().port();
        // }

        java.util.Map<String, String> kafkaParams = new HashMap<String, String>();
        // kafkaParams.put("metadata.broker.list", "localhost:" + embeddedKafka.kafkaConfig().port());
        kafkaParams.put("metadata.broker.list", brokers);
        Set<String> topicsSet = new HashSet<>(Arrays.asList(KafkaTopicRaw));
        //printClassPath();

        JavaPairInputDStream<String, String> rootStream = null;
        try {
            rootStream = KafkaUtils.createDirectStream(
                    ssc,
                    String.class,
                    String.class,
                    StringDecoder.class,
                    StringDecoder.class,
                    kafkaParams,
                    topicsSet
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaDStream<RawPowerData> powerStream = rootStream
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(" "))
                .map((Function<String[], RawPowerData>) array -> new RawPowerData(array));

        /** Saves the raw data to Cassandra - raw table. */
        powerStream.foreachRDD((JavaRDD<RawPowerData> x) -> {
            if (x.count() <= 0) {
                return;
            }

            javaFunctions(x).writerBuilder(CassandraKeyspace, CassandraTableRaw, mapToRow(RawPowerData.class))
                    .saveToCassandra();

            // saveToEs(x, "spark/power");
        });

        powerStream.map((Function<RawPowerData, HourlyPowerData>) raw -> new HourlyPowerData(raw))
                .foreachRDD((JavaRDD<HourlyPowerData> x) -> {
                    if (x.count() <= 0) {
                        return;
                    }
                    javaFunctions(x).writerBuilder(CassandraKeyspace, HourlyCassandraTable, mapToRow(HourlyPowerData.class))
                            .saveToCassandra();
                });


        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
    }

    private void printClassPath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

    public static void main(String[] args) {
        new PowerKafkaStreaming().run(args);
    }
}
