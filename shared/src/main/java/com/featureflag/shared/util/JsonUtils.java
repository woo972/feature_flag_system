package com.featureflag.shared.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.shared.config.JacksonConfig;
import com.featureflag.shared.exception.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public final class JsonUtils {

    private JsonUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        return JacksonConfig.getObjectMapper();
    }

    public static ObjectMapper copyObjectMapper() {
        return JacksonConfig.copyObjectMapper();
    }

    public static <T> T readValue(String content) {
        return readValue(getObjectMapper(), content, new TypeReference<T>() {});
    }

    public static <T> T readValue(ObjectMapper mapper, String content, Class<T> type) {
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            log.error("Failed to read value from json", e);
            throw new JsonProcessingException("Failed to deserialize JSON to %s".formatted(type.getSimpleName()), e);
        }
    }

    public static <T> T readValue(ObjectMapper mapper, String content, JavaType type) {
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            log.error("Failed to read value from json", e);
            throw new JsonProcessingException("Failed to deserialize JSON to %s".formatted(type), e);
        }
    }

    public static <T> T readValue(ObjectMapper mapper, String content, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(content, typeReference);
        } catch (Exception e) {
            log.error("Failed to read value from json", e);
            throw new JsonProcessingException("Failed to deserialize JSON using type reference", e);
        }
    }

    public static <T> T readValue(String content, TypeReference<T> typeReference) {
        return readValue(getObjectMapper(), content, typeReference);
    }

    public static <T> List<T> readListValue(String content, Class<T> itemType) {
        return readListValue(getObjectMapper(), content, itemType);
    }

    public static <T> List<T> readListValue(ObjectMapper mapper, String content, Class<T> itemType) {
        try {
            return mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, itemType));
        } catch (Exception e) {
            log.error("Failed to read list value from json", e);
            throw new JsonProcessingException("Failed to deserialize JSON array to List<%s>".formatted(itemType.getSimpleName()), e);
        }
    }

    public static String writeValue(ObjectMapper mapper, Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Failed to write value to json", e);
            throw new JsonProcessingException("Failed to serialize %s to JSON".formatted(value.getClass().getSimpleName()), e);
        }
    }
}
