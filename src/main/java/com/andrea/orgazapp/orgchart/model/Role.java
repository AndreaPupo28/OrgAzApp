package com.andrea.orgazapp.orgchart.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Role {
    private String name;
    private final Set<String> applicableUnits;

    public Role(String name, Set<String> applicableUnits) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo non può essere nullo o vuoto.");
        }
        this.name = name;
        this.applicableUnits = (applicableUnits == null || applicableUnits.isEmpty())
                ? Set.of("DefaultUnit")
                : applicableUnits;
    }

    public Role() { //costruttore per Jackson
        this.name = "";
        this.applicableUnits = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    protected boolean isValidForUnit(String unitType) {
        if (unitType == null) {
            return false;
        }
        return applicableUnits.contains(unitType);
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
        Role role = (Role) obj;
        return name.equalsIgnoreCase(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

}

