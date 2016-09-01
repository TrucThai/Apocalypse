package com.biglabs.tool;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thainguy on 9/1/2016.
 */
public class FileUtils {
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

}
