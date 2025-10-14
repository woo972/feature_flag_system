package com.featureflag.shared.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonConfig {
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JacksonConfig() {
        // Private constructor to prevent instantiation
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Configure serialization
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // Don't serialize null values
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  // Use ISO-8601 date format
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  // Don't fail on empty beans

        // Configure deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown properties
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);  // Allow single values as arrays
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);  // Treat empty strings as null
        mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true); // 단일 값 배열을 자동으로 풀어서 처리 (예: ["value"] -> "value")
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); // 무시된 속성에 대해 실패하지 않음 (무시 어노테이션이 있는 속성이 JSON에 있어도 오류 발생 안 함)
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false); // 유효하지 않은 하위 타입에 대해 실패하지 않음 (다형성 처리 시 유연성 제공)
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false); // 생성자에 필요한 속성이 누락되어도 실패하지 않음 (@JsonCreator 생성자의 필수 매개변수 누락 시 오류 발생 안 함)
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false); // 생성자에 null 값이 전달되어도 실패하지 않음 (생성자 매개변수에 null 허용)
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false); // 기본 타입(int, boolean 등)에 null 값이 할당되어도 실패하지 않음 (기본값 사용)
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false); // enum 값에 숫자가 사용되어도 실패하지 않음 (enum에 숫자 매핑 허용)
        mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false); // JSON 트리에 중복 키가 있어도 실패하지 않음 (마지막 값으로 덮어씀)
        mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false); // 해결되지 않은 객체 ID가 있어도 실패하지 않음 (@JsonIdentityReference 사용 시 유연성 제공)
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true); // 알 수 없는 enum 값을 null로 처리 (정의되지 않은 enum 값을 null로 변환)

        return mapper;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static ObjectMapper copyObjectMapper() {
        return OBJECT_MAPPER.copy();
    }
}
