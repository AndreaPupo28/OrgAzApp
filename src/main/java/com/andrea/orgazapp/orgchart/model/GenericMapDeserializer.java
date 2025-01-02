package com.andrea.orgazapp.orgchart.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class GenericMapDeserializer<T> extends JsonDeserializer<Map<String, T>> {

    private Function<JsonNode, T> objectCreator;

    //Costruttore vuoto richiesto da Jackson
    public GenericMapDeserializer() {
    }

    public GenericMapDeserializer(Function<JsonNode, T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    @Override
    public Map<String, T> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (objectCreator == null) {
            throw new IllegalStateException("La funzione objectCreator non è stata configurata.");
        }

        JsonNode node = jp.getCodec().readTree(jp);
        Map<String, T> resultMap = new HashMap<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                T object = objectCreator.apply(item);
                String name = item.get("name").asText();
                resultMap.put(name, object);
            }
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                T object = objectCreator.apply(entry.getValue());
                resultMap.put(key, object);
            }
        }
        return resultMap;
    }
}
