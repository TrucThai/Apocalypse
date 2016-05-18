package com.biglabs.apocalypse.timeseries;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.google.common.collect.Lists;
import org.apache.spark.api.java.AbstractJavaRDDLike;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.python.DoubleArrayToWritableConverter;
import org.apache.spark.storage.StorageLevel;
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
    public void run(Map<String, String> kafkaParams, JavaStreamingContext ssc) {
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put("topic", 1);
        JavaDStream<RawWeatherData> kafkaStream = KafkaUtils.createStream(
                ssc, "zkQuorum", "groupId", topicMap)
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(","))
                .map((Function<String[], RawWeatherData>) array -> new RawWeatherData(array));
//        .map(_._2.split(","))
//                .map(RawWeatherData(_))

        /** Saves the raw data to Cassandra - raw table. */
        //kafkaStream.saveToCassandra(CassandraKeyspace, CassandraTableRaw)

        kafkaStream.foreachRDD(new VoidFunction<JavaRDD<RawWeatherData>>() {
            @Override
            public void call(JavaRDD<RawWeatherData> x) throws Exception {
                javaFunctions(x).writerBuilder("java_api", "summaries", mapToRow(RawWeatherData.class))
                        .saveToCassandra();
            }
        });
    }
}
