package com.featureflag.shared.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.*;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RuleOperatorTest {

    @DisplayName("test IN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a', a, true",
            "'a,b', a, true",
            "'a', ab, false"
    })
    public void testInOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.IN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test NOT_IN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a', a, false",
            "'a,b', a, false",
            "'a', ab, true"
    })
    public void testNotInOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.NOT_IN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a', a, true",
            "'a,b', a, false",
            "'a', ab, false"
    })
    public void testEqualOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test NOT_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a', a, false",
            "'a,b', a, false",
            "'a', ab, true"
    })
    public void testNotEqualOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.NOT_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test GREATER_THAN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a, b', b, false",
            "'a', b, true",
            "'b', a, false",
            "'1', 2, true",
            "'2', 1, false",
            "'2', 10, true",
            "'10', 2, false"
    })
    public void testGreaterThanOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.GREATER_THAN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test LESS_THAN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a, b', b, false",
            "'a', b, false",
            "'b', a, true",
            "'1', 2, false",
            "'2', 1, true",
            "'2', 10, false",
            "'10', 2, true"
    })
    public void testLessThanOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.LESS_THAN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test GREATER_THAN_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a, b', b, false",
            "'a', b, true",
            "'b', a, false",
            "'1', 2, true",
            "'2', 1, false",
            "'2', 10, true",
            "'10', 2, false",
            "'a', a, true",
            "'1', 1, true"
    })
    public void testGreaterThanEqualOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.GREATER_THAN_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test LESS_THAN_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "'a, b', b, false",
            "'a', b, false",
            "'b', a, true",
            "'1', 2, false",
            "'2', 1, true",
            "'2', 10, false",
            "'10', 2, true",
            "'a', a, true",
            "'1', 1, true"
    })
    public void testLessThanEqualOperator(@ConvertWith(StringToListConverter.class) List<String> expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.LESS_THAN_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    static class StringToListConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) {
            if (source instanceof String && List.class.isAssignableFrom(targetType)) {
                return Arrays.asList(((String) source).split(","));
            }
            throw new IllegalArgumentException("Conversion from " + source.getClass() + " to " + targetType + " not supported");
        }
    }
}