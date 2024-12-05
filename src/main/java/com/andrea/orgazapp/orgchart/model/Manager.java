package com.andrea.orgazapp.orgchart.model;

public class Manager extends OrgNode {

    public Manager() {
        super(); // Costruttore vuoto per Jackson
    }

    public Manager(String name) {
        super(name);
        this.type = "Manager"; // Assicurati che il tipo sia impostato
    }


    @Override
    public String getType() {
        return "Manager";
    }
}