package com.andrea.orgazapp.orgchart.factory;


import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.Manager;
import com.andrea.orgazapp.orgchart.model.OrgNode;

public class OrgNodeFactory {

    public OrgNode createNode(String type, String name) {
        return switch (type.toLowerCase()) {
            case "manager" -> new Manager(name);
            case "employee" -> new Employee(name);
            default -> throw new IllegalArgumentException("Tipo non valido: " + type);
        };
    }
}

