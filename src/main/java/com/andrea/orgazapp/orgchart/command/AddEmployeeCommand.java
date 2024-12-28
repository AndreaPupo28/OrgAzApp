package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

import java.util.Objects;

public class AddEmployeeCommand implements Command {
    private final OrgNode targetNode;
    private final Employee employee;
    private final Role role;

    public AddEmployeeCommand(OrgNode targetNode, Employee employee, Role role) {
        this.targetNode = targetNode;
        this.employee = employee;
        this.role = role;
    }

    @Override
    public boolean doIt() {
        try {
            targetNode.addEmployee(employee);
            targetNode.assignRole(employee, role);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            targetNode.getEmployees().remove(employee);
            targetNode.getRoles().remove(employee.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AddEmployeeCommand that = (AddEmployeeCommand) obj;
        return targetNode.equals(that.targetNode) &&
                employee.equals(that.employee) &&
                role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetNode, employee, role);
    }


}