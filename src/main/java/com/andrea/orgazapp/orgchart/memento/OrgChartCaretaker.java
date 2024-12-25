package com.andrea.orgazapp.orgchart.memento;


import com.andrea.orgazapp.orgchart.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class OrgChartCaretaker {

    private final ObjectMapper mapper;

    public OrgChartCaretaker() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        SimpleModule employeeModule = new SimpleModule();
        employeeModule.addDeserializer(Map.class, new EmployeeMapDeserializer());
        this.mapper.registerModule(employeeModule);
        SimpleModule roleListModule = new SimpleModule();
        roleListModule.addDeserializer(Map.class, new RoleMapDeserializer());
        this.mapper.registerModule(roleListModule);
        SimpleModule roleAssocModule = new SimpleModule();
        roleAssocModule.addDeserializer(Map.class, new RoleAssociationMapDeserializer());
        this.mapper.registerModule(roleAssocModule);
    }




    public void saveToFile(OrgNode orgChart, String filePath) {
        try {
            String json = mapper.writeValueAsString(orgChart);
            System.out.println(json);

            Files.writeString(Paths.get(filePath), json);

        } catch (IOException e) {
            System.err.println("ERRORE durante il salvataggio del file: " + e.getMessage());
            throw new RuntimeException("Errore durante il salvataggio su file.", e);
        } catch (Exception e) {
            System.err.println("ERRORE inaspettato: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Errore inaspettato durante il salvataggio.", e);
        }
    }


    public OrgNode loadFromFile(String filePath, Class<? extends OrgNode> rootType) {
        try {
            String json = Files.readString(Paths.get(filePath));
            OrgNode orgNode = mapper.readValue(json, rootType);
            Map<String, Role> loadedRoles = orgNode.getRoles();
            orgNode.reconcileRoles(loadedRoles);

            return orgNode;
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il caricamento dal file.", e);
        }
    }

}
