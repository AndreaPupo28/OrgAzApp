package com.andrea.orgazapp.orgchart.model;

public class Employee extends OrgNode {

    public Employee() {
        super();
    }

    public Employee(String name) {
        super(name);
        this.type = "Employee";
    }

    @Override
    public String toString() {
        return getName();
    }

}