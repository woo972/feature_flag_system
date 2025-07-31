package com.featureflag.shared.model;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum RuleOperator {
    IN((List<String> expectedValues, String actualValue) -> expectedValues.stream().anyMatch(
            expectedValue -> expectedValue.equalsIgnoreCase(actualValue)
    )),
    NOT_IN((List<String> expectedValues, String actualValue) -> expectedValues.stream().noneMatch(
            expectedValue -> expectedValue.equalsIgnoreCase(actualValue)
    )),
    EQUAL((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        return expectedValue.equalsIgnoreCase(actualValue);
    }),
    NOT_EQUAL((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        return !expectedValue.equalsIgnoreCase(actualValue);
    }),
    GREATER_THAN((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual > expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) < 0;
        }
    }),
    LESS_THAN((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual < expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) > 0;
        }
    }),
    GREATER_THAN_EQUAL((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual >= expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) <= 0;
        }
    }),
    LESS_THAN_EQUAL((List<String> expectedValues, String actualValue) -> {
        if(expectedValues.size() != 1) return false;
        var expectedValue = expectedValues.getFirst();
        try {
            var expected = Double.parseDouble(expectedValue);
            var actual = Double.parseDouble(actualValue);
            return actual <= expected;
        } catch (NumberFormatException e) {
            return expectedValue.compareTo(actualValue) >= 0;
        }
    }),
    ;

    private final BiFunction<List<String>, String, Boolean> matcher;

    public boolean matches(List<String> expectedValues, String actualValue) {
        return matcher.apply(expectedValues, actualValue);
    }
}

