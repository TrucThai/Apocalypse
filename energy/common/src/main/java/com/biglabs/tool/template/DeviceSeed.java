package com.biglabs.tool.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by thainguy on 9/5/2016.
 */
public class DeviceSeed {
    private String fileName;
    private String workingDir;
    private ArrayList<Seed> data;

    public DeviceSeed(String fileName, String workingDir, ArrayList<Seed> data) {
        this.fileName = fileName;
        this.workingDir = workingDir;
        this.data = data;
    }

    public DeviceSeed(String fileName, String workingDir){
        this.fileName = fileName;
        this.workingDir = workingDir;
    }

    public void init() throws Exception{
        File file = new File(workingDir, fileName);
        Reader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        data = new ArrayList<>();
        while (line != null) {
            Seed seed = new Seed(line);
            data.add(seed);
            line = bufferedReader.readLine();
        }
    }

    public ArrayList<Seed> getData() {
        return data;
    }

    public void setData(ArrayList<Seed> data) {
        this.data = data;
    }

    public static class Seed{
        private long time;
        private String data;

        public Seed(){}

        public Seed(long time, String data){
            this.time = time;
            this.data = data;
        }

        public Seed(String s){
            String[] splits = s.split(" ");
            this.time = Long.parseLong(splits[0]);
            this.data = splits[1];
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
