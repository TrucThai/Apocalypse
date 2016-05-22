package com.biglabs.apocalypse.timeseries;

import com.datastax.spark.connector.embedded.EmbeddedKafka;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kafka.serializer.StringDecoder;
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
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.*;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

/**
 * Created by tail on 5/19/2016.
 */
public class WeatherKafkaStreaming {
    String sparkMaster = "local[*]";
    String cassandraHosts = "localhost";
    int sparkCleanerTtl = 3600*2;
    long SparkStreamingBatchInterval = 1000;

    public void run() {
        Config rootConf = ConfigFactory.load();
        Config kafka = rootConf.getConfig("kafka");
        Config killrweather = rootConf.getConfig("killrweather");
        String CassandraKeyspace = killrweather.getString("cassandra.keyspace");
        String CassandraTableRaw = killrweather.getString("cassandra.table.raw");
        String KafkaGroupId = kafka.getString("group.id");
        String KafkaTopicRaw = kafka.getString("topic.raw");

        SparkConf conf = new SparkConf()
                .setAppName(WeatherKafkaStreaming.class.getSimpleName())
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHosts)
//                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//                .set("spark.kryo.registrator", "com.datastax.killrweather.KillrKryoRegistrator")
                .set("spark.cleaner.ttl", String.valueOf(sparkCleanerTtl));

        // es

        conf.set("es.index.auto.create", "true");

       // JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.milliseconds(SparkStreamingBatchInterval));
        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(2000));


        /** Starts the Kafka broker and Zookeeper. */
        EmbeddedKafka embeddedKafka = new EmbeddedKafka();

        /** Creates the raw data topic. */
        embeddedKafka.createTopic(KafkaTopicRaw, 1, 1);
        String zkQuorum = embeddedKafka.kafkaConfig().zkConnect();

//        HashSet<String> topicsSet = new HashSet<>(Arrays.asList("topic"));
//        HashMap<String, String> kafkaParams = new HashMap<String, String>();
//        String brokers = "localhost";
//        kafkaParams.put("metadata.broker.list", brokers);
//
//        // Create direct kafka stream with brokers and topics
//        JavaPairInputDStream<String, String> rootStream = KafkaUtils.createDirectStream(
//                ssc,
//                String.class,
//                String.class,
//                StringDecoder.class,
//                StringDecoder.class,
//                kafkaParams,
//                topicsSet
//        );


        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(KafkaTopicRaw, 1);

        JavaPairReceiverInputDStream<String, String> rootStream = KafkaUtils.createStream(ssc,
                zkQuorum, KafkaGroupId, topicMap);


        JavaDStream<RawWeatherData> kafkaStream = rootStream
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(","))
                .map((Function<String[], RawWeatherData>) array -> new RawWeatherData(array));

        /** Saves the raw data to Cassandra - raw table. */
        kafkaStream.foreachRDD((JavaRDD<RawWeatherData> x) -> {
            javaFunctions(x).writerBuilder(CassandraKeyspace, CassandraTableRaw, mapToRow(RawWeatherData.class))
                    .saveToCassandra();

            saveToEs(x, "spark/docs");
        });

        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
    }

    public static void main(String[] args)  {
        new WeatherKafkaStreaming().run();
    }
}
