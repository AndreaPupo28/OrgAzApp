package com.andrea.orgazapp.orgchart.factory;


import com.andrea.orgazapp.orgchart.model.Department;
import com.andrea.orgazapp.orgchart.model.Manager;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.WorkGroup;

public class OrgNodeFactory {

    public static OrgNode createNode(String type, String name) {
        OrgNode node;
        switch (type.toLowerCase()) {
            case "manager":
                node = new Manager(name);
                break;
            case "department":
                node = new Department(name);
                break;
            case "workgroup":
                node = new WorkGroup(name);
                break;
            default:
                throw new IllegalArgumentException("Tipo di nodo non supportato: " + type);
        }
        return node;
    }

}

