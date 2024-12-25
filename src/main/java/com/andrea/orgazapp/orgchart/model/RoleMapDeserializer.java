package com.andrea.orgazapp.orgchart.model;

public class RoleMapDeserializer extends GenericMapDeserializer<Role> {
    public RoleMapDeserializer() {
        super(node -> new Role(node.get("name").asText(), null));
    }
}
