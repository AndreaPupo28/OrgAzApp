package com.andrea.orgazapp.orgchart.model;

import com.andrea.orgazapp.orgchart.observer.OrgChartObserver;
import java.util.*;
import com.andrea.orgazapp.orgchart.memento.Memento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.andrea.orgazapp.orgchart.memento.Originator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


//DP Composite

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Management.class, name = "Management"),
        @JsonSubTypes.Type(value = Department.class, name = "Department"),
        @JsonSubTypes.Type(value = WorkGroup.class, name = "WorkGroup")
})
public abstract class OrgNode implements Originator {
    private String name;
    private List<OrgNode> children = new ArrayList<>();
    protected String type;

    @JsonDeserialize(using = RoleAssociationMapDeserializer.class)
    private Map<String, Role> roles = new HashMap<>(); // Ruoli associati ai dipendenti

    @JsonDeserialize(using = EmployeeMapDeserializer.class)
    private Map<String, Employee> employees = new HashMap<>();

    @JsonDeserialize(using = RoleMapDeserializer.class)
    private Map<String, Role> rolesList = new HashMap<>();

    // Costruttore vuoto per Jackson
    public OrgNode() {}

    @JsonIgnore
    private final List<OrgChartObserver> observers = new ArrayList<>();

    public OrgNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
        notifyObservers();
    }

    public void assignRole(Employee employee, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Ruolo non valido: il ruolo è null.");
        }
        roles.put(employee.getName(), role);
        notifyObservers();
    }

    public void reconcileRoles(Map<String, Role> newRoles) {
        for (Map.Entry<String, Role> entry : newRoles.entrySet()) {
            String employeeName = entry.getKey();
            Role newRole = entry.getValue();
            if (employees.containsKey(employeeName)) {
                assignRole(employees.get(employeeName), newRole);
            } else {
                System.out.println("Dipendente non trovato: " + employeeName);
            }
        }
        notifyObservers();
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public String getType() {
        return type;
    }

    public Collection<Employee> getEmployees() {
        return employees.values();
    }

    public Collection<Role> getRolesList() {
        return rolesList.values();
    }

    public void addEmployee(Employee employee) {
        if (employees.containsKey(employee.getName())) {
            throw new IllegalArgumentException("Dipendente già presente in questo nodo.");
        }
        employees.put(employee.getName(), employee);
        notifyObservers();
    }

    public void addRole(Role role) {
        if (rolesList.containsKey(role.getName())) {
            throw new IllegalArgumentException("Ruolo già esistente: " + role.getName());
        }
        rolesList.put(role.getName(), role);
        notifyObservers();
    }


    public boolean containsNodeWithName(String name) {
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }
        for (OrgNode child : children) {
            if (child.containsNodeWithName(name)) {
                return true;
            }
        }
        return false;
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
        if (!(memento instanceof OrgNodeMemento orgNodeMemento)) {
            throw new IllegalArgumentException("Memento non valido.");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            OrgNode restoredNode = mapper.readValue(orgNodeMemento.state(), this.getClass());
            this.name = restoredNode.name;
            this.children = restoredNode.children;
            this.type = restoredNode.type;
            this.roles = restoredNode.roles;
            this.employees = restoredNode.employees != null ? restoredNode.employees : new HashMap<>();
            this.rolesList = restoredNode.rolesList != null ? restoredNode.rolesList : new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il ripristino del Memento", e);
        }
    }

    public List<OrgNode> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrgNode orgNode = (OrgNode) obj;
        return name.equalsIgnoreCase(orgNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

}