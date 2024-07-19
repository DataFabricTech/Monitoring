package com.mobigen.monitoring.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public JsonNode getJsonNode(String jsonStr) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        return mapper.readTree(jsonStr);
    }
}
