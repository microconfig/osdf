package io.microconfig.osdf.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONUtils {
    public static <T> T create(String repr, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(repr, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create object from string representation", e);
        }
    }
}
