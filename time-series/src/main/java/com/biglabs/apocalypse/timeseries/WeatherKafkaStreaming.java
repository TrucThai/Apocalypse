package com.biglabs.apocalypse.timeseries;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.google.common.collect.Lists;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.AbstractJavaRDDLike;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.python.DoubleArrayToWritableConverter;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

/**
 * Created by tail on 5/19/2016.
 */
public class WeatherKafkaStreaming {
    String sparkMaster = "local[*]";
    String cassandraHosts = "";
    int sparkCleanerTtl = 3600*2;
    long SparkStreamingBatchInterval = 1000;
    String keyspace = "isd_weather_data";
    String tableRaw = "raw_weather_data";


    public void run() {
        SparkConf conf = new SparkConf()
                .setAppName(WeatherKafkaStreaming.class.getSimpleName())
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHosts)
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", "com.datastax.killrweather.KillrKryoRegistrator")
                .set("spark.cleaner.ttl", String.valueOf(sparkCleanerTtl));

        JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.milliseconds(SparkStreamingBatchInterval));

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put("topic", 1);
        JavaDStream<RawWeatherData> kafkaStream = KafkaUtils.createStream(
                ssc, "zkQuorum", "groupId", topicMap)
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(","))
                .map((Function<String[], RawWeatherData>) array -> new RawWeatherData(array));

        /** Saves the raw data to Cassandra - raw table. */
        kafkaStream.foreachRDD((JavaRDD<RawWeatherData> x) -> {
            javaFunctions(x).writerBuilder(keyspace, tableRaw, mapToRow(RawWeatherData.class))
                    .saveToCassandra();
        });

        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
    }

    public static void main(String[] args)  {
        new WeatherKafkaStreaming().run();
    }
}
