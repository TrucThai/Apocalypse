package com.biglabs.tool.model.poco;

import com.sun.javafx.sg.prism.NGShape;

import java.io.Serializable;

/**
 * Created by thainguy on 9/1/2016.
 */
public class ModelBase implements Serializable {
    protected String id;
    protected String name;
    protected String desc;

    public ModelBase(){}

    public ModelBase(String id, String name, String desc){
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
