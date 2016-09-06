package com.biglabs.tool;

import com.biglabs.tool.model.HouseTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thainguy on 9/5/2016.
 */
public class HouseTemplateLoader extends LoaderBase<HouseTemplate>{

    @Override
    protected HouseTemplate create(String line) throws Exception{
        HouseTemplate ht = new HouseTemplate(line);
        ht.init();
        return ht;
    }
}
