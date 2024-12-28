package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.andrea.orgazapp.orgchart.model.Role;

import java.util.Objects;

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
        try {
            return targetNode.getRolesList().remove(role);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AddRoleCommand that = (AddRoleCommand) obj;
        return targetNode.equals(that.targetNode) &&
                role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetNode, role);
    }

}
