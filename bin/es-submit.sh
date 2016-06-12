#!/bin/bash
echo 'Staring EsIngestionApp...'
/opt/iot/master-spark/spark-1.6-bin-custom-spark/bin/spark-submit --class com.biglabs.apocalypse.timeseries.es.EsIngestionApp --master spark://192.168.1.131:7077 ./es-ingestion/target/es-ingestion-1.0.0-SNAPSHOT-jar-with-dependencies.jar