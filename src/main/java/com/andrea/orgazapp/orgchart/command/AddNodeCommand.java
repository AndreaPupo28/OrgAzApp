package com.andrea.orgazapp.orgchart.command;

import com.andrea.orgazapp.orgchart.model.OrgNode;

import java.util.Objects;

public class AddNodeCommand implements Command {
    private OrgNode parentNode;
    private OrgNode newNode;

    public AddNodeCommand(OrgNode parentNode, OrgNode newNode) {
        this.parentNode = parentNode;
        this.newNode = newNode;
    }

    @Override
    public boolean doIt() {
        if (parentNode.containsNodeWithName(newNode.getName())) {
            System.out.println("Errore: Un nodo con questo nome esiste già.");
            return false;
        }
        parentNode.addChild(newNode);
        return true;
    }


    @Override
    public boolean undoIt() {
        try {
            return parentNode.removeNode(newNode.getName());
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AddNodeCommand that = (AddNodeCommand) obj;
        return parentNode.equals(that.parentNode) &&
                newNode.equals(that.newNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentNode, newNode);
    }



}

