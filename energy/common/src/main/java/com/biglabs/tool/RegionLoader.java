package com.biglabs.tool;

import com.biglabs.tool.model.Region;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thainguy on 9/1/2016.
 */
public class RegionLoader extends LoaderBase<Region> {

    @Override
    protected Region create(String line) {
        String[] lineSplit = line.split(" ");
        Region region = new Region(lineSplit[0], lineSplit[1], "");
        return region;
    }

}
