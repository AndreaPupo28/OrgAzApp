package com.andrea.orgazapp.orgchart.model;

public class WorkGroup extends OrgNode {

    public WorkGroup() {
        super();
    }

    public WorkGroup(String name) {
        super(name);
        this.type = "WorkGroup";
    }
}