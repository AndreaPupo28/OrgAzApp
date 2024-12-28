package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;

import java.util.Objects;

public class ModifyNodeNameCommand implements Command {
    private final OrgNode node;
    private final String oldName;
    private final String newName;

    public ModifyNodeNameCommand(OrgNode node, String oldName, String newName) {
        this.node = node;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public boolean doIt() {
        try {
            node.setName(newName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            node.setName(oldName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ModifyNodeNameCommand that = (ModifyNodeNameCommand) obj;
        return node.equals(that.node) &&
                oldName.equals(that.oldName) &&
                newName.equals(that.newName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, oldName, newName);
    }

}
