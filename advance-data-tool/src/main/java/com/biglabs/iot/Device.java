package com.biglabs.iot;


/**
 * Created by thainguy on 6/7/2016.
 */
public class Device implements Runnable{
    private DataPublisher publisher;
    private DataSeed dataSeed;
    private int dataCursor = -1;

    public DataPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(DataPublisher publisher) {
        this.publisher = publisher;
    }

    public DataSeed getDataSeed() {
        return dataSeed;
    }

    public void setDataSeed(DataSeed dataSeed) {
        this.dataSeed = dataSeed;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;

    public Device(){

    }

    public Device(DataPublisher publisher, DataSeed dataSeed, String header){
        this.publisher = publisher;
        this.dataSeed = dataSeed;
        this.header = header;
    }

    @Override
    public void run() {
        dataCursor ++;
        if(dataCursor >= dataSeed.getData().size()){
            dataCursor = 0;
        }
        String seed = dataSeed.getData().get(dataCursor);
        String data = header + " " + System.currentTimeMillis()/1000 + " " + seed;
        publisher.send(data);
    }
}
