package com.andrea.orgazapp.orgchart.model;

public class Department extends OrgNode {

    public Department() {
        super();
    }

    public Department(String name) {
        super(name);
        this.type = "Department";
    }
}
