package com.biglabs.tool.model.poco;

import java.io.Serializable;

/**
 * Created by thainguy on 9/1/2016.
 */
public class Device extends ModelBase implements Serializable{
    private String houseId;

    public Device(){}

    public Device(String id, String name, String desc, String houseId){
        super(id, name, desc);
        this.houseId = houseId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }
}
