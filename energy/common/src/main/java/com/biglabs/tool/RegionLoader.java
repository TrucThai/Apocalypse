package com.biglabs.tool;

import com.biglabs.tool.model.poco.Region;

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
