package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;

public class AddNodeCommand implements Command {
    private OrgNode parentNode;
    private OrgNode newNode;

    public AddNodeCommand(OrgNode parentNode, OrgNode newNode) {
        this.parentNode = parentNode;
        this.newNode = newNode;
    }

    @Override
    public boolean doIt() {
        parentNode.addChild(newNode);
        return true;
    }


    @Override
    public boolean undoIt() {
        try {
            parentNode.removeNode(newNode.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
