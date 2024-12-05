package com.andrea.orgazapp.orgchart.command;


import com.andrea.orgazapp.orgchart.model.OrgNode;

public class RemoveNodeCommand implements Command {
    private OrgNode parentNode;
    private OrgNode removedNode;

    public RemoveNodeCommand(OrgNode parentNode, OrgNode removedNode) {
        this.parentNode = parentNode;
        this.removedNode = removedNode;
    }

    @Override
    public boolean doIt() {
        try {
            parentNode.removeNode(removedNode.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        try {
            parentNode.addChild(removedNode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
