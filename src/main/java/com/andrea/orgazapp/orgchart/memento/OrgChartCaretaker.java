package com.andrea.orgazapp.orgchart.memento;


import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OrgChartCaretaker {

    private final ObjectMapper mapper;

    public OrgChartCaretaker() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
            System.out.println(json);

            return mapper.readValue(json, rootType);

        } catch (IOException e) {
            System.err.println("ERRORE durante il caricamento dal file: " + e.getMessage());
            throw new RuntimeException("Errore durante il caricamento dal file.", e);
        } catch (Exception e) {
            System.err.println("ERRORE inaspettato: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Errore inaspettato durante il caricamento.", e);
        }
    }
}
