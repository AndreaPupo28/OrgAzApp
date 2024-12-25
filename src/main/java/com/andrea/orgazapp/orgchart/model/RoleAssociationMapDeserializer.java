package com.andrea.orgazapp.orgchart.model;

public class RoleAssociationMapDeserializer extends GenericMapDeserializer<Role> {
    public RoleAssociationMapDeserializer() {
        super(node -> new Role(node.get("name").asText(), null));
    }
}