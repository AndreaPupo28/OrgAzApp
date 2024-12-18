package com.andrea.orgazapp.orgchart.model;


public class Management extends OrgNode {

    public Management() {
        super(); // Costruttore vuoto per Jackson
    }

    public Management(String name) {
        super(name);
        this.type = "Management";
    }


    @Override
    public String getType() {
        return "Management";
    }
}