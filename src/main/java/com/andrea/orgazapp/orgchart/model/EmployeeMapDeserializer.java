package com.andrea.orgazapp.orgchart.model;

public class EmployeeMapDeserializer extends GenericMapDeserializer<Employee> {
    public EmployeeMapDeserializer() {
        super(node -> new Employee(node.get("name").asText()));
    }
}