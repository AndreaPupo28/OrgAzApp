package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

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
            targetNode.getRoles().remove(employee);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}