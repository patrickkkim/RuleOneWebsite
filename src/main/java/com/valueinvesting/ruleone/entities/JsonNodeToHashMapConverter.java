package com.valueinvesting.ruleone.entities;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonNodeToHashMapConverter {

    public HashMap<String, Object> convertJsonNodeToHashMap(JsonNode jsonNode) {
        HashMap<String, Object> hashMap = new HashMap<>();

        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode valueNode = entry.getValue();
                Object value = convertJsonNodeToObject(valueNode);
                hashMap.put(key, value);
            });
        }

        return hashMap;
    }

    public Object convertJsonNodeToObject(JsonNode jsonNode) {
        if (jsonNode.isObject()) return convertJsonNodeToHashMap(jsonNode);
        else if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>();
            jsonNode.elements().forEachRemaining(element ->
                    list.add(convertJsonNodeToObject(element)));
            return list;
        }
        else if (jsonNode.isTextual()) return jsonNode.textValue();
        else if (jsonNode.isBoolean()) return jsonNode.booleanValue();
        else if (jsonNode.isNumber()) return jsonNode.numberValue();
        else return null;
    }
}
