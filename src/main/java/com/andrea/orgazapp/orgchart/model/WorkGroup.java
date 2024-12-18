package com.andrea.orgazapp.orgchart.model;

public class WorkGroup extends OrgNode {

    public WorkGroup() {
        super();
    }

    public WorkGroup(String name) {
        super(name);
        this.type = "WorkGroup";
    }

    @Override
    public void addChild(OrgNode node) {
        throw new UnsupportedOperationException("Un WorkGroup non può avere figli.");
    }

    @Override
    public boolean removeNode(String name) {
        throw new UnsupportedOperationException("Un WorkGroup non può rimuovere figli.");
    }
}