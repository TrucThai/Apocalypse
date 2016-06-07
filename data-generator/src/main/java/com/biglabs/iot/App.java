package com.biglabs.iot;

import java.io.*;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        if (args.length < 4) {
            System.err.println("Usage: data-tool <dataRoot> <destRoot>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <destRoot> is a list of one or more kafka topics to publish to\n\n");
            System.exit(1);
        }

        String dataRoot = args[0];
        String destRoot = args[1];

        long startTime = Long.getLong(args[2]);
        long endTime = Long.getLong(args[3]);


        System.out.println("dataroot " + dataRoot);
        System.out.println("destRoot " + destRoot);

        ArrayList<File> files = new ArrayList<File>();
        listFiles(dataRoot, files);

        if(files.size() == 0){
            System.err.println("The data directory doesn't contain any file");
            System.exit(2);
        }

        System.out.println("Number of files to process: " + files.size());

        for(File file: files){
            try{
                if(file.getName().startsWith("channel")) {
                    System.out.println("processing: " + file.getAbsolutePath());

                    String destFileName = file.getAbsolutePath().replaceFirst(dataRoot, destRoot);
                    System.out.println("dest: " + destFileName);
                    File destFile = new File(destFileName);
                    if(destFile.exists() && destFile.isDirectory()){
                        System.err.println("directory is exist with same name");
                        continue;
                    }

                    Reader reader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    ArrayList<String> fileData = new ArrayList<>();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        fileData.add(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();

                    if(fileData.size() < 2){
                        System.out.println("File is empty");
                    }

                    String startLine = fileData.get(0);
                    String endLine = fileData.get(fileData.size() - 1);

                    long fileDataStartTime = Long.getLong(startLine.split(" ")[0]);
                    long fileDataEndTime = Long.getLong(endLine.split(" ")[0]);

                    long duration = fileDataEndTime - fileDataStartTime;
                    long startDiff = fileDataStartTime - startTime;
                    long iter = 0;
                    boolean isEnd = false;

                    Writer fileWriter = new FileWriter(destFileName);
                    PrintWriter writer = new PrintWriter(fileWriter);

                    while(!isEnd){
                        long shiftTime = iter * duration - startDiff;
                        for(String data: fileData){
                            String[] dataSplit = data.split(" ");
                            if(dataSplit.length < 2){
                                continue;
                            }
                            long dataTime = Long.getLong(dataSplit[0]);
                            long writeTime = dataTime + shiftTime;
                            if(writeTime > endTime){
                                isEnd = true;
                                break;
                            }

                            writer.println(writeTime + " " + dataSplit[1]);
                        }
                        iter ++;
                    }

                    writer.close();

                }
            } catch (Exception ex){
                System.err.println(ex.toString());
            }
        }
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
