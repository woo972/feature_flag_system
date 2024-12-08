package com.featureflag.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonConfig {
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JacksonConfig() {
        // Private constructor to prevent instantiation
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure serialization
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // Don't serialize null values
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  // Use ISO-8601 date format
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  // Don't fail on empty beans
        
        // Configure deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown properties
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);  // Allow single values as arrays
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);  // Treat empty strings as null
        
        // Register modules
        mapper.registerModule(new JavaTimeModule());  // Support Java 8 date/time types
        
        return mapper;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
