package com.valueinvesting.ruleone.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        String hashMapJson = null;
        try {
            hashMapJson = objectMapper.writeValueAsString(stringObjectMap);
        } catch (final JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return hashMapJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        if (s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') {
            s = s.substring(1, s.length() - 1);
        }
        s = s.replaceAll("\\\\", "");
        Map<String, Object> hashMap = null;
        try {
            hashMap = objectMapper.readValue(s, new TypeReference<HashMap<String, Object>>() {});
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
        return hashMap;
    }
}
