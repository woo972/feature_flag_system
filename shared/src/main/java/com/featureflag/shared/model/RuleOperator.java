package com.featureflag.shared.model;

import lombok.RequiredArgsConstructor;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum RuleOperator {
    IN(String::equals),
    NOT_IN((String expectedValue, String actualValue) -> !expectedValue.equals(actualValue)),
    EQUAL(String::equals),
    NOT_EQUAL((String expectedValue, String actualValue) -> !expectedValue.equals(actualValue)),
    GREATER_THAN((String expectedValue, String actualValue) -> {
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual > expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) < 0;
        }
    }),
    LESS_THAN((String expectedValue, String actualValue) -> {
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual < expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) > 0;
        }
    }),
    GREATER_THAN_EQUAL((String expectedValue, String actualValue) -> {
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual >= expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) <= 0;
        }
    }),
    LESS_THAN_EQUAL((String expectedValue, String actualValue) -> {
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual <= expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) >= 0;
        }
    }),
    ;

    private final BiFunction<String, String, Boolean> matcher;

    public boolean matches(String expectedValue, String actualValue) {
        return matcher.apply(expectedValue, actualValue);
    }
}

