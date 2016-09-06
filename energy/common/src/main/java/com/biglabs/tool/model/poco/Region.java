package com.biglabs.tool.model.poco;

import java.io.Serializable;

/**
 * Created by thainguy on 9/1/2016.
 */
public class Region extends ModelBase implements Serializable{

    public Region(){
    }

    public Region(String id, String name, String desc){
        super(id, name, desc);
    }
}
