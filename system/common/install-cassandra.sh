#!/bin/sh

# install cassandra
sudo echo "deb http://debian.datastax.com/datastax-ddc 3.5 main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list

sudo curl -L https://debian.datastax.com/debian/repo_key | sudo apt-key add -

sudo apt-get update
sudo apt-get install datastax-ddc


