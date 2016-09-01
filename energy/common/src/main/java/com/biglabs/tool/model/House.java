package com.biglabs.tool.model;

import java.io.Serializable;

/**
 * Created by thainguy on 9/1/2016.
 */
public class House extends ModelBase implements Serializable{
    private String regionId;

    public House(){}

    public House(String id, String name, String desc, String regionId){
        super(id, name, desc);
        this.regionId = regionId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
