package com.andrea.orgazapp.orgchart.command;


import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

import java.util.HashMap;
import java.util.Map;

public class RemoveRoleCommand implements Command {
    private final OrgNode node;
    private final Role role;
    private final Map<Employee, Role> removedEmployees = new HashMap<>(); // Per ripristinare con undo

    public RemoveRoleCommand(OrgNode node, Role role) {
        this.node = node;
        this.role = role;
    }

    @Override
    public boolean doIt() {
        try {
            node.getEmployees().removeIf(employee -> {
                Role assignedRole = node.getRoles().get(employee);
                if (assignedRole != null && assignedRole.equals(role)) {
                    removedEmployees.put(employee, assignedRole);
                    return true;
                }
                return false;
            });
            return node.getRolesList().remove(role);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            node.getRolesList().add(role);
            removedEmployees.forEach(node::assignRole);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
