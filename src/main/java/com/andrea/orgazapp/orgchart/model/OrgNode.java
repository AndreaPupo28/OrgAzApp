package com.andrea.orgazapp.orgchart.model;

import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import java.util.*;
import com.andrea.orgazapp.orgchart.memento.Memento;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.andrea.orgazapp.orgchart.memento.Originator;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Manager.class, name = "Manager"),
        @JsonSubTypes.Type(value = Employee.class, name = "Employee"),
        @JsonSubTypes.Type(value = Department.class, name = "Department"),
        @JsonSubTypes.Type(value = WorkGroup.class, name = "WorkGroup")
})
public abstract class OrgNode implements Originator{
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

    public List<OrgNode> getChildren() {
        return children;
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

    @Override
    public Memento saveToMemento() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonState = mapper.writeValueAsString(this);
            return new OrgNodeMemento(jsonState);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la creazione del Memento", e);
        }
    }

    @Override
    public void restoreFromMemento(Memento memento) {
        if (!(memento instanceof OrgNodeMemento)) {
            throw new IllegalArgumentException("Memento non valido.");
        }

        OrgNodeMemento orgNodeMemento = (OrgNodeMemento) memento;
        try {
            ObjectMapper mapper = new ObjectMapper();
            OrgNode restoredNode = mapper.readValue(orgNodeMemento.getState(), this.getClass());
            this.name = restoredNode.name;
            this.children = restoredNode.children;
            this.type = restoredNode.type;
            this.roles = restoredNode.roles;
            this.employees = restoredNode.employees;
            this.rolesList = restoredNode.rolesList;
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il ripristino del Memento", e);
        }
    }

    private static class OrgNodeMemento implements Memento {
        private final String state;

        OrgNodeMemento(String state) {
            this.state = state;
        }

        String getState() {
            return state;
        }
    }


}
