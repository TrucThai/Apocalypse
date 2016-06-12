#!/bin/bash
pwd
echo 'Staring PowerKafkaStreaming...'
/opt/iot/master-spark/spark-1.6-bin-custom-spark/bin/spark-submit --class com.biglabs.apocalypse.timeseries.PowerKafkaStreaming --master spark://192.168.1.131:7077 ./time-series/target/timeseries-1.0.0-SNAPSHOT-jar-with-dependencies.jar
