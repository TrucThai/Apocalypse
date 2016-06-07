package com.biglabs.iot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thainguy on 6/7/2016.
 */
public class Util {

    public static ArrayList<File> listFiles(String dir) {
        ArrayList<File> files = new ArrayList<>();
        listFiles(dir, files);
        return files;
    }

    public static void listFiles(String dir, ArrayList<File> files) {
        File dirFile = new File(dir);
        if (dirFile.isDirectory()) {
            File[] fileList = dirFile.listFiles();
            for (File file : fileList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listFiles(file.getAbsolutePath(), files);
                }
            }
        } else if (dirFile.isFile()) {
            files.add(dirFile);
        }

    }

    public static Map<String, DataSeed> load(String seedRoot) {
        ArrayList<File> files = Util.listFiles(seedRoot);
        Map<String, DataSeed> seeds = new HashMap<>();
        for (File file : files) {
            try {
                if (file.getName().startsWith("channel")) {
                    System.out.println("Loading seed: " + file.getAbsolutePath());
                    Reader reader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    ArrayList<String> seedData = new ArrayList<>();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        seedData.add(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();

                    DataSeed seed = new DataSeed(file.getName(), file.getAbsolutePath(), seedData);
                    seeds.put(seed.getFileName(), seed);
                }
            } catch (Exception ex) {
                System.err.println(ex.toString());
            }
        }

        return seeds;
    }

    public static List<Device> loadDevices(String deviceConf, Map<String, DataSeed> seeds, DataPublisher publisher) throws Exception {
        File file = new File(deviceConf);
        if (!file.exists()) {
            throw new Exception(deviceConf + " doesn't exist");
        }
        if (file.isDirectory()) {
            throw new Exception(deviceConf + " is directory.");
        }

        List<Device> devices = new ArrayList<>();
        Reader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (line != null) {
            try{
                String[] lineSplit = line.split(" ");
                String[] channelConfs = lineSplit[1].split(",");
                for (String str: channelConfs){
                    if(str.contains("-")){
                        String[] channelRange = str.split("-");
                        int startRange = Integer.getInteger(channelRange[0]);
                        int endRange = Integer.getInteger(channelRange[1]);
                        for(int i = startRange; i <= endRange; i ++){
                            int channelNo = i;
                            String header = lineSplit[0] + " chanel_" + channelNo;
                            String seedKey = "chanel_" + channelNo + ".dat";
                            Device device = new Device(publisher, seeds.get(seedKey), header);
                            devices.add(device);
                        }
                    } else{
                        int channelNo = Integer.getInteger(str);
                        String header = lineSplit[0] + " chanel_" + channelNo;
                        String seedKey = "chanel_" + channelNo + ".dat";
                        Device device = new Device(publisher, seeds.get(seedKey), header);
                        devices.add(device);
                    }
                }
            } catch (Exception ex){
                System.err.println(ex);
            }

            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return devices;
    }
}
