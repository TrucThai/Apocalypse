package com.biglabs.apocalypse.timeseries;

import com.biglabs.apocalypse.timeseries.model.*;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.execution.columnar.BOOLEAN;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.saveToEs;
import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;
/**
 * Created by tail on 5/28/2016.
 */
public class PowerKafkaStreaming {
    int sparkCleanerTtl = 3600 * 2;

    public void run(String[] args) {

        ClassLoader classLoader = getClass().getClassLoader();

        Config rootConf = ConfigFactory.parseResources(classLoader, "apocalypse.conf");
        System.out.print(rootConf.entrySet());
        Config kafka = rootConf.getConfig("kafka");
        String KafkaTopicRaw = kafka.getString("topic.power");
        String brokers = kafka.getString("hosts");
        Config apocalypse = rootConf.getConfig("apocalypse");
        String CassandraKeyspace = apocalypse.getString("cassandra.power.keyspace");
        String CassandraTableRaw = apocalypse.getString("cassandra.power.table.raw");
        String CassandraTableHouseHourly = apocalypse.getString("cassandra.power.table.house_hourly");
        String CassandraTableHouseDaily = apocalypse.getString("cassandra.power.table.house_daily");
        String CassandraTableRegionHourly = apocalypse.getString("cassandra.power.table.region_hourly");
        String CassandraTableRegionDaily = apocalypse.getString("cassandra.power.table.region_daily");
        String HourlyCassandraTable = apocalypse.getString("cassandra.power.table.hourly_power_data");

        Config spark = rootConf.getConfig("spark");
        String sparkMaster = spark.getString("master");// "local[*]";
        Config cassandra = rootConf.getConfig("cassandra");
        String cassandraHosts = cassandra.getString("connection.host");//"localhost";

        SparkConf conf = new SparkConf()
                .setAppName(PowerKafkaStreaming.class.getSimpleName())
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHosts)
                .set("spark.cleaner.ttl", String.valueOf(sparkCleanerTtl))
                .set("spark.executor.memory", "8g")
                //.set("spark.eventLog.enabled", "true")
                .set("spark.logConf", "true")
                .set("spark.cassandra.output.batch.size.rows", "400")
                .set("spark.cassandra.output.concurrent.writes", "100")
                .set("spark.cassandra.output.batch.size.bytes", "2000000")
                .set("spark.executor.cores", "5")
                .set("spark.cassandra.connection.keep_alive_ms", "60000");

        // es
//        conf.set("es.index.auto.create", "true");
//        conf.set("es.nodes", "192.168.1.84");
//        conf.set("es.port", "9200");
//        conf.set("es.nodes.wan.only", "true");

        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(15000));

        java.util.Map<String, String> kafkaParams = new HashMap<String, String>();
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

        // Raw power data
        JavaDStream<PowerRaw> rawpowerStream = rootStream
                .map((Function<Tuple2<String, String>, String[]>) tuple2 -> tuple2._2().split(" "))
                .map((Function<String[], PowerRaw>) array -> {
                    return new PowerRaw(array);
                });

        CassandraStreamingJavaUtil.javaFunctions(rawpowerStream)
                .writerBuilder(CassandraKeyspace, CassandraTableRaw, mapToRow(PowerRaw.class))
                .saveToCassandra();

        // Filter to use channel_1 (aggreated value)
        JavaDStream<PowerRaw> aggPowerStream = rawpowerStream.filter(powerraw -> {
            return powerraw.getDevice().endsWith("_1");
        } );

        // House hourly
        JavaDStream<HouseHourly> houseHourlyStream = aggPowerStream.map(powerraw -> new HouseHourly(powerraw));
        CassandraStreamingJavaUtil.javaFunctions(houseHourlyStream)
                .writerBuilder(CassandraKeyspace, CassandraTableHouseHourly, mapToRow(HouseHourly.class))
                .saveToCassandra();

        // House daily
        JavaDStream<HouseDaily> houseDailyStream = houseHourlyStream.map(houseHourly -> new HouseDaily(houseHourly));
        CassandraStreamingJavaUtil.javaFunctions(houseDailyStream)
                .writerBuilder(CassandraKeyspace, CassandraTableHouseDaily, mapToRow(HouseDaily.class))
                .saveToCassandra();

        // Region hourly
        JavaDStream<RegionHourly> regionHourlyStream = aggPowerStream.map(powerraw -> new RegionHourly(powerraw));
        CassandraStreamingJavaUtil.javaFunctions(regionHourlyStream)
                .writerBuilder(CassandraKeyspace, CassandraTableRegionHourly, mapToRow(RegionHourly.class))
                .saveToCassandra();

        // Region daily
        JavaDStream<RegionDaily> regionDailyStream = regionHourlyStream.map(regionHourly -> new RegionDaily(regionHourly));
        CassandraStreamingJavaUtil.javaFunctions(regionDailyStream)
                .writerBuilder(CassandraKeyspace, CassandraTableRegionDaily, mapToRow(RegionDaily.class))
                .saveToCassandra();

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
