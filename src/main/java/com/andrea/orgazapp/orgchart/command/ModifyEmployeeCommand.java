package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.Employee;
import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

public class ModifyEmployeeCommand implements Command {
    private final OrgNode node;
    private final Employee employee;
    private final Role oldRole;
    private final Role newRole;
    private final String oldName;
    private final String newName;

    public ModifyEmployeeCommand(OrgNode node, Employee employee, Role oldRole, Role newRole, String oldName, String newName) {
        this.node = node;
        this.employee = employee;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public boolean doIt() {
        try {
            employee.setName(newName);
            node.assignRole(employee, newRole);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            employee.setName(oldName);
            node.assignRole(employee, oldRole);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}