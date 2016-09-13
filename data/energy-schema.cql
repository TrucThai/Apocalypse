
DROP KEYSPACE IF EXISTS power_data;
CREATE KEYSPACE power_data WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

use power_data;

CREATE TABLE raw_power_data (
   home text,
   device text,
   time timestamp,
   value int,
   PRIMARY KEY ((home), device, time)
);

CREATE TABLE zone_hourly (
   zone text,
   year int,
   month int,
   day int,
   hour int,
   aggregate_counter counter,
   value counter,
   PRIMARY KEY ((zone), year, month, day, hour)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE zone_daily (
   zone text,
   year int,
   month int,
   day int,   
   aggregate_counter counter,
   value counter,
   PRIMARY KEY ((zone), year, month, day)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC);

CREATE TABLE house_hourly (
   house text,
   year int,
   month int,
   day int,
   hour int,
   aggregate_counter counter,
   value counter,
   PRIMARY KEY ((house), year, month, day, hour)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE house_daily (
   house text,
   year int,
   month int,
   day int,
   aggregate_counter counter,
   value counter,
   PRIMARY KEY ((house), year, month, day)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC);