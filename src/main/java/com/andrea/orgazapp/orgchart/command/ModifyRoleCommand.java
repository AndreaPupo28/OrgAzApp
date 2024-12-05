package com.andrea.orgazapp.orgchart.command;


import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

public class ModifyRoleCommand implements Command {
    private final OrgNode node;
    private final Role oldRole;
    private final String newRoleName;
    private final String oldRoleName;

    public ModifyRoleCommand(OrgNode node, Role role, String newRoleName) {
        this.node = node;
        this.oldRole = role;
        this.newRoleName = newRoleName;
        this.oldRoleName = role.getName();
    }

    @Override
    public boolean doIt() {
        try {
            oldRole.setName(newRoleName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            oldRole.setName(oldRoleName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
