package com.andrea.orgazapp.orgchart.model;

import java.util.HashSet;
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
                ? Set.of("Default") // Fallback
                : applicableUnits;
    }

    public Role() {
        this.name = ""; // Valore di fallback
        this.applicableUnits = new HashSet<>();
    }


    public String getName() {
        return name;
    }

    public boolean isValidForUnit(String unitType) {
        if (unitType == null) {
            return false;
        }
        return applicableUnits != null && applicableUnits.contains(unitType);
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}

