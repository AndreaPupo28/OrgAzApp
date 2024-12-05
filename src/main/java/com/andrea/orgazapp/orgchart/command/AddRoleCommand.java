package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

public class AddRoleCommand implements Command {
    private final OrgNode targetNode;
    private final Role role;

    public AddRoleCommand(OrgNode targetNode, Role role) {
        this.targetNode = targetNode;
        this.role = role;
    }


    @Override
    public boolean doIt() {
        try {
            targetNode.addRole(role);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        return targetNode.getRolesList().remove(role);
    }
}
