package com.andrea.orgazapp.orgchart.command;


import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RemoveRoleCommand implements Command {
    private final OrgNode node;
    private final Role role;
    private final Map<Employee, Role> removedEmployeeRoles = new HashMap<>();

    public RemoveRoleCommand(OrgNode node, Role role) {
        this.node = node;
        this.role = role;
    }

    @Override
    public boolean doIt() {
        try {
            node.getEmployees().forEach(employee -> {
                Role assignedRole = node.getRoles().get(employee.getName());
                if (assignedRole != null && assignedRole.equals(role)) {
                    removedEmployeeRoles.put(employee, assignedRole);
                    node.getRoles().remove(employee.getName());
                }
            });

            node.getRolesList().removeIf(r -> r.getName().equalsIgnoreCase(role.getName()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            node.addRole(role);
            removedEmployeeRoles.forEach((employee, restoredRole) -> {
                node.getRoles().put(employee.getName(), restoredRole);
            });
            removedEmployeeRoles.clear();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RemoveRoleCommand that = (RemoveRoleCommand) obj;
        return node.equals(that.node) &&
                role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, role);
    }

}
