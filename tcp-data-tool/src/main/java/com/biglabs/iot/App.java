package com.biglabs.iot;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: data-tool <brokers> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <topics> is a list of one or more kafka topics to publish to\n\n");
            System.exit(1);
        }

        String address = args[0];
        String port = args[1];
        String dataRoot = args[2];


        System.out.println("address " + address);
        System.out.println("port " + port);
        System.out.println("dataroot " + dataRoot);

        Socket clientSocket = new Socket(address, Integer.parseInt(port));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        ArrayList<File> files = new ArrayList<File>();
        listFiles(dataRoot, files);

        if(files.size() == 0){
            System.err.println("The data directory doesn't contain any file");
            System.exit(2);
        }

        long startTime = System.currentTimeMillis();
        long numberMsg = 0;
        for(File file: files){

            try{
                if(file.getName().startsWith("channel")) {
                    System.out.println("processing: " + file.getAbsolutePath());
                    String parent = file.getParentFile().getName();
                    String name = file.getName();
                    String header = parent + " " + name + " ";
                    Reader reader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line = bufferedReader.readLine();

                    while (line != null) {
                        numberMsg++;
                        outToServer.writeBytes(line);
                        line = bufferedReader.readLine();
                    }

                    bufferedReader.close();
                }
            } catch (Exception ex){
                System.err.println(ex.toString());
            }

        }

        clientSocket.close();

        long endTime = System.currentTimeMillis();
        System.out.println("total: " + numberMsg);
        System.out.println("Total time: " + (endTime - startTime));
    }

    public static void listFiles(String dir, ArrayList<File> files){
        File dirFile = new File(dir);
        if(dirFile.isDirectory()){
            File[] fileList = dirFile.listFiles();
            for(File file: fileList){
                if(file.isFile()){
                    files.add(file);
                } else if(file.isDirectory()){
                    listFiles(file.getAbsolutePath(), files);
                }
            }
        } else if(dirFile.isFile()){
            files.add(dirFile);
        }

    }
}
