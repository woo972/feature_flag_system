package com.featureflag.sdk.config;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JsonConfig {
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static <T> T readValue(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<T>() {});
        } catch (Exception e) {
            log.error("Failed to read value from json", e);
            throw new RuntimeException(e);
        }
    }
}
