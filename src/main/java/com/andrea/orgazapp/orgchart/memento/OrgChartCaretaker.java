package com.andrea.orgazapp.orgchart.memento;


import com.andrea.orgazapp.orgchart.model.OrgNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
        this.mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
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
            json = json.replaceAll("\"employees\"\\s*:\\s*\\[\\s*\\{\\s*\"name\"\\s*:\\s*\"(.*?)\"\\s*}\\s*]",
                    "\"employees\": { \"$1\": { \"name\": \"$1\" } }");
            json = json.replaceAll("\"rolesList\"\\s*:\\s*\\[\\s*\\{\\s*\"name\"\\s*:\\s*\"(.*?)\"\\s*}\\s*]",
                    "\"rolesList\": { \"$1\": { \"name\": \"$1\" } }");
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
