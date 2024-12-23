package com.andrea.orgazapp.orgchart.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlexibleMapDeserializer extends JsonDeserializer<Map<String, Role>> {

    @Override
    public Map<String, Role> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Map<String, Role> resultMap = new HashMap<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                String name = item.get("name").asText();
                Role role = new Role(name, null);
                resultMap.put(name, role);
            }
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                String name = entry.getValue().get("name").asText();
                Role role = new Role(name, null);
                resultMap.put(key, role);
            }
        }
        return resultMap;
    }
}
