package com.mobigen.monitoring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public JsonNode getJsonNode(String jsonStr) {
        var mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean stringToBoolean(String booleanStr) {
        return booleanStr.equalsIgnoreCase("successful");
    }

}
