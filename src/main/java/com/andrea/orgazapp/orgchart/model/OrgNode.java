package com.andrea.orgazapp.orgchart.model;

import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import java.util.*;

public abstract class OrgNode {
    protected String name;
    public List<OrgNode> children = new ArrayList<>();
    protected String type;

    private Map<String, Role> roles = new HashMap<>();

    private List<Employee> employees = new ArrayList<>();
    private List<Role> rolesList = new ArrayList<>();

    public OrgNode() {}

    private final List<OrgChartObserver> observers = new ArrayList<>();

    public OrgNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyObservers();
    }

    public void addObserver(OrgChartObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrgChartObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (OrgChartObserver observer : observers) {
            observer.onOrgChartUpdated();
        }
    }

    public void addChild(OrgNode node) {
        children.add(node);
        notifyObservers();
    }

    public boolean removeNode(String name) {
        boolean removed = children.removeIf(node -> node.getName().equals(name));
        if (removed) {
            notifyObservers();
        }
        return removed;
    }


    public void assignRole(Employee employee, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Ruolo non valido: il ruolo è null.");
        }
        if (this.type == null) {
            throw new IllegalArgumentException("Tipo di unità organizzativa non valido: il tipo è null.");
        }
        if (!role.isValidForUnit(this.type)) {
            throw new IllegalArgumentException("Ruolo non valido per questa unità organizzativa. Tipo: " + this.type);
        }
        roles.put(employee.getName(), role);
        notifyObservers();
    }



    public Map<String, Role> getRoles() {
        return roles;
    }

    public String getType() {
        return type;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Role> getRolesList() {
        return rolesList;
    }

    public void addEmployee(Employee employee) {
        Optional<Employee> existingEmployee = employees.stream()
                .filter(e -> e.getName().equalsIgnoreCase(employee.getName()))
                .findFirst();
        if (existingEmployee.isPresent()) {
            throw new IllegalArgumentException("Dipendente già esistente: " + employee.getName());
        }
        employees.add(employee);
    }

    public void addRole(Role role) {
        Optional<Role> existingRole = rolesList.stream()
                .filter(r -> r.getName().equalsIgnoreCase(role.getName()))
                .findFirst();
        if (existingRole.isPresent()) {
            throw new IllegalArgumentException("Ruolo già esistente: " + role.getName());
        }
        rolesList.add(role);
    }

    public OrgNode findNodeParent(String childName) {
        for (OrgNode child : children) {
            if (child.getName().equals(childName)) {
                return this;
            }
            OrgNode result = child.findNodeParent(childName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
