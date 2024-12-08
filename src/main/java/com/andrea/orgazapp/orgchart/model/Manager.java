package com.andrea.orgazapp.orgchart.model;

public class Manager extends OrgNode {

    public Manager(String name){
        super(name);
        this.type = "Manager";
    }

    @Override
    public String getType() {
        return "Manager";
    }
}