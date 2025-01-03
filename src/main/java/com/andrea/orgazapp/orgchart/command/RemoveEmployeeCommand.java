package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

import java.util.Objects;

public class RemoveEmployeeCommand implements Command {
    private final OrgNode node;
    private final Employee employee;
    private Role removedRole;

    public RemoveEmployeeCommand(OrgNode node, Employee employee) {
        this.node = node;
        this.employee = employee;
    }

    @Override
    public boolean doIt() {
        try {
            removedRole = node.getRoles().get(employee.getName());
            if (removedRole != null) {
                node.getRoles().remove(employee.getName());
            }
            node.getEmployees().remove(employee);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            node.addEmployee(employee);
            if (removedRole != null) {
                node.assignRole(employee, removedRole);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RemoveEmployeeCommand that = (RemoveEmployeeCommand) obj;
        return node.equals(that.node) &&
                employee.equals(that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, employee);
    }

}
