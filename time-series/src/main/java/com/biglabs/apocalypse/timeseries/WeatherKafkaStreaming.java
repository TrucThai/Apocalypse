package com.biglabs.apocalypse.timeseries;

import com.datastax.spark.connector.embedded.EmbeddedKafka;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.mapper.TupleColumnMapper;
import com.datastax.spark.connector.writer.RowWriterFactory;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import groovy.lang.Tuple;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.AbstractJavaRDDLike;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.python.DoubleArrayToWritableConverter;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCRDD;
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
import scala.Tuple5;
import scala.collection.immutable.*;
import scala.collection.immutable.Map;

import java.io.Serializable;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void run(String[] args) {
        Config rootConf = ConfigFactory.load();
        Config kafka = rootConf.getConfig("kafka");
        Config killrweather = rootConf.getConfig("killrweather");
        String CassandraKeyspace = killrweather.getString("cassandra.keyspace");
        String CassandraTableRaw = killrweather.getString("cassandra.table.raw");
        String KafkaGroupId = kafka.getString("group.id");
        String KafkaTopicRaw = kafka.getString("topic.raw");
        String CassandraTableDailyPrecip = killrweather.getString("cassandra.table.daily.precipitation");

        Config spark = rootConf.getConfig("spark");
        String sparkMaster = spark.getString("master");// "local[*]";
        Config cassandra = rootConf.getConfig("cassandra");
        String cassandraHosts = cassandra.getString("connection.host");//"localhost";

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


        String brokers;
        if (args.length > 0) {
            brokers = args[0];
        } else {
            /** Starts the Kafka broker and Zookeeper. */
            EmbeddedKafka embeddedKafka = new EmbeddedKafka();

            /** Creates the raw data topic. */
            embeddedKafka.createTopic(KafkaTopicRaw, 1, 1);
            brokers = embeddedKafka.kafkaConfig().hostName() + ":" + embeddedKafka.kafkaConfig().port();
        }


        //Set<String> topicsSet = new HashSet<>(Arrays.asList(KafkaTopicRaw));
//        HashMap<String, String> kafkaParams = new HashMap<String, String>();
//        String brokers = "localhost";
//        kafkaParams.put("metadata.broker.list", brokers);

        // Create direct kafka stream with brokers and topics
        //Set<String> topicsSet = null;
        //Map<String, String> kafkaParams = new HashMap<String, String>(embeddedKafka.kafkaParams());
        java.util.Map<String, String> kafkaParams = new HashMap<String, String>();
        // kafkaParams.put("metadata.broker.list", "localhost:" + embeddedKafka.kafkaConfig().port());
        kafkaParams.put("metadata.broker.list", brokers);
        Set<String> topicsSet = new HashSet<>(Arrays.asList(KafkaTopicRaw));
        JavaPairInputDStream<String, String> rootStream = KafkaUtils.createDirectStream(
                ssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
        );

//        String zkQuorum = embeddedKafka.kafkaConfig().zkConnect();
//        Map<String, Integer> topicMap = new HashMap<>();
//        topicMap.put(KafkaTopicRaw, 1);
//
//        JavaPairReceiverInputDStream<String, String> rootStream = KafkaUtils.createStream(ssc,
//                zkQuorum, KafkaGroupId, topicMap);


        JavaDStream<RawWeatherData> kafkaStream = rootStream
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(","))
                .map((Function<String[], RawWeatherData>) array -> new RawWeatherData(array));

        /** Saves the raw data to Cassandra - raw table. */
        kafkaStream.foreachRDD((JavaRDD<RawWeatherData> x) -> {
            if (x.count() <= 0) {
                return;
            }
            javaFunctions(x).writerBuilder(CassandraKeyspace, CassandraTableRaw, mapToRow(RawWeatherData.class))
                    .saveToCassandra();

            saveToEs(x, "spark/docs");
        });

        /** For a given weather station, year, month, day, aggregates hourly precipitation values by day.
         * Weather station first gets you the partition key - data locality - which spark gets via the
         * connector, so the data transfer between spark and cassandra is very fast per node.
         *
         * Persists daily aggregate data to Cassandra daily precip table by weather station,
         * automatically sorted by most recent (due to how we set up the Cassandra schema:
         * @see https://github.com/killrweather/killrweather/blob/master/data/create-timeseries.cql.
         *
         * Because the 'oneHourPrecip' column is a Cassandra Counter we do not have to do a spark
         * reduceByKey, which is expensive. We simply let Cassandra do it - not expensive and fast.
         * This is a Cassandra 2.1 counter functionality ;)
         *
         * This new functionality in Cassandra 2.1.1 is going to make time series work even faster:
         * https://issues.apache.org/jira/browse/CASSANDRA-6602
         */
        kafkaStream.map((Function<RawWeatherData, Tuple5<String, Integer, Integer, Integer, Double>>)
                weather -> new Tuple5(weather.wsid, weather.year, weather.month, weather.day, weather.oneHourPrecip)
        ).foreachRDD((JavaRDD<Tuple5<String, Integer, Integer, Integer, Double>> x) -> {
            if (x.count() <= 0) {
                return;
            }

            RowWriterFactory<Tuple5<String, Integer, Integer, Integer, Double>> tuple5RowWriterFactory
                    = CassandraJavaUtil.mapTupleToRow(String.class, Integer.class, Integer.class, Integer.class, Double.class);
            javaFunctions(x).writerBuilder(CassandraKeyspace, CassandraTableDailyPrecip, tuple5RowWriterFactory)
                    .saveToCassandra();

//            saveToEs(x, "aggregated/daily_precip");
        });

        kafkaStream.map((Function<RawWeatherData, DailyPrecipitation>)
                weather -> new DailyPrecipitation(weather.wsid, weather.year, weather.month, weather.day, weather.oneHourPrecip)
        ).foreachRDD((JavaRDD<DailyPrecipitation> x) -> {
            if (x.count() <= 0) {
                return;
            }

            saveToEs(x, "spark/daily_precip");
        });

        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
    }

    public static void main(String[] args)  {
        new WeatherKafkaStreaming().run(args);
    }
}
