package com.biglabs.tool;

import com.biglabs.tool.template.HouseTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thainguy on 9/5/2016.
 */
public class TemplateLoader {
    public Map<String, HouseTemplate> load(String configFile) throws Exception {
        File file = new File(configFile);
        if (!file.exists()) {
            throw new Exception(configFile + " doesn't exist");
        }
        if (file.isDirectory()) {
            throw new Exception(configFile + " is directory.");
        }

        Map<String, HouseTemplate> regions = new HashMap<>();
        Reader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (line != null) {
            HouseTemplate model = create(line);
            regions.put(model.getName(), model);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return regions;
    }

    protected HouseTemplate create(String line) throws Exception{
        HouseTemplate ht = new HouseTemplate(line);
        ht.init();
        return ht;
    }
}
