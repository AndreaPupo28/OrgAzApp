package com.andrea.orgazapp.orgchart.model;

import java.util.Objects;

public class Employee {
    private String name;

    public Employee(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del dipendente non può essere vuoto.");
        }
        this.name = name;
    }

    // Costruttore vuoto per Jackson
    public Employee() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return name.equalsIgnoreCase(employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

}