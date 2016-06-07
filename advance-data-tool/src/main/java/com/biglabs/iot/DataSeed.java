package com.biglabs.iot;

import java.util.ArrayList;

/**
 * Created by thainguy on 6/7/2016.
 */
public class DataSeed {
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    private String fileName;
    private String fullPath;
    private ArrayList<String> data;

    public DataSeed(String fileName, String fullPath, ArrayList<String> data) {
        this.fileName = fileName;
        this.fullPath = fullPath;
        this.data = data;
    }
}
