
DROP KEYSPACE IF EXISTS power_data;
CREATE KEYSPACE power_data WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

use power_data;

CREATE TABLE raw_power_data (
   region text,
   house text,
   device text,
   time timestamp,
   value int,
   PRIMARY KEY ((house), device, time)
)
 WITH CLUSTERING ORDER BY (time DESC);

CREATE TABLE region_hourly (
   region text,
   year int,
   month int,
   day int,
   hour int,
   aggcouner counter,
   value counter,
   PRIMARY KEY ((region), year, month, day, hour)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE region_daily (
   region text,
   year int,
   month int,
   day int,   
   aggcouner counter,
   value counter,
   PRIMARY KEY ((region), year, month, day)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC);

CREATE TABLE house_hourly (
   house text,
   year int,
   month int,
   day int,
   hour int,
   aggcouner counter,
   value counter,
   PRIMARY KEY ((house), year, month, day, hour)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC, hour DESC);

CREATE TABLE house_daily (
   house text,
   year int,
   month int,
   day int,
   aggcouner counter,
   value counter,
   PRIMARY KEY ((house), year, month, day)
)  WITH CLUSTERING ORDER BY (year DESC, month DESC, day DESC);