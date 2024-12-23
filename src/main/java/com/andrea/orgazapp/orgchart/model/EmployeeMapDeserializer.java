package com.andrea.orgazapp.orgchart.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EmployeeMapDeserializer extends JsonDeserializer<Map<String, Employee>> {

    @Override
    public Map<String, Employee> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Map<String, Employee> resultMap = new HashMap<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                String name = item.get("name").asText();
                Employee employee = new Employee(name);
                resultMap.put(name, employee);
            }
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                String name = entry.getValue().get("name").asText();
                Employee employee = new Employee(name);
                resultMap.put(key, employee);
            }
        }
        return resultMap;
    }
}
