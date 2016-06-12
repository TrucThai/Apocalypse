package com.biglabs.apocalypse.timeseries.es;
import com.biglabs.apocalypse.timeseries.RawPowerData;
import com.biglabs.apocalypse.timeseries.HourlyPowerData;
import com.datastax.spark.connector.ColumnRef;
import com.datastax.spark.connector.cql.TableDef;
import com.datastax.spark.connector.embedded.EmbeddedKafka;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.datastax.spark.connector.japi.DStreamJavaFunctions;
import com.datastax.spark.connector.rdd.reader.RowReader;
import com.datastax.spark.connector.rdd.reader.RowReaderFactory;
import com.datastax.spark.connector.writer.RowWriterFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kafka.serializer.StringDecoder;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import scala.Tuple2;
import scala.Tuple5;
import scala.collection.IndexedSeq;

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
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapColumnTo;
import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.saveToEs;
import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;
/**
 * Created by tai on 6/12/16.
 */
public class EsIngestionApp {
    int sparkCleanerTtl = 3600 * 2;

    public void run(String[] args) {
        ClassLoader classLoader = getClass().getClassLoader();
        Config rootConf = ConfigFactory.parseResources(classLoader, "apocalypse.conf");
        System.out.print(rootConf.entrySet());

        Config apocalypse = rootConf.getConfig("apocalypse");
        String CassandraKeyspace = apocalypse.getString("cassandra.power.keyspace");
        String CassandraTableRaw = apocalypse.getString("cassandra.power.table.raw");
        String HourlyCassandraTable = apocalypse.getString("cassandra.power.table.hourly_power_data");

        Config spark = rootConf.getConfig("spark");
        String sparkMaster = spark.getString("master");// "local[*]";
        Config cassandra = rootConf.getConfig("cassandra");
        String cassandraHosts = cassandra.getString("connection.host");//"localhost";

        SparkConf conf = new SparkConf()
                .setAppName(EsIngestionApp.class.getSimpleName())
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHosts)
                .set("spark.cleaner.ttl", String.valueOf(sparkCleanerTtl))
                .set("spark.executor.memory", "1g")
                .set("spark.cassandra.output.batch.size.rows", "5120")
                .set("spark.cassandra.output.concurrent.writes", "100")
                .set("spark.cassandra.output.batch.size.bytes", "100000")
                .set("spark.cassandra.connection.keep_alive_ms", "60000");

        // elastic search
        conf.set("es.index.auto.create", "true");

        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(5000));

        JavaRDD<HourlyPowerData> hourlyPowerRDD = javaFunctions(ssc)
                .cassandraTable(CassandraKeyspace, HourlyCassandraTable, CassandraJavaUtil.mapRowTo(HourlyPowerData.class));

        JavaEsSpark.saveToEs(hourlyPowerRDD, "docs/hourly_power");

        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
    }

    public static void main(String[] args) {
        new EsIngestionApp().run(args);
    }

}
