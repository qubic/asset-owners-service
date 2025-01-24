package org.qubic.aos.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper JSON = new ObjectMapper();

    public static <T> T fromJson(final String json, final Class<T> clazz) {
        try {
            // log.info("Converting json: {}", json);
            return JSON.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Could not convert from json to {}: {}", clazz.getSimpleName(), json);
            throw new RuntimeException("Could not convert from json.", e);
        }
    }

}
