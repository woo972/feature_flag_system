package com.featureflag.shared.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class RuleOperatorTest {

    @DisplayName("test IN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, a, true",
            "ab, a, false",
            "a, ab, false"
    })
    public void testInOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.IN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test NOT_IN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, a, false",
            "ab, a, true",
            "a, ab, true"
    })
    public void testNotInOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.NOT_IN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, a, true",
            "ab, a, false",
            "a, ab, false"
    })
    public void testEqualOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test NOT_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, a, false",
            "ab, a, true",
            "a, ab, true"
    })
    public void testNotEqualOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.NOT_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test GREATER_THAN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, b, true",
            "b, a, false",
            "1, 2, true",
            "2, 1, false",
            "2, 10, true",
            "10, 2, false",

    })
    public void testGreaterThanOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.GREATER_THAN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test LESS_THAN operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, b, false",
            "b, a, true",
            "1, 2, false",
            "2, 1, true",
            "2, 10, false",
            "10, 2, true",
    })
    public void testLessThanOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.LESS_THAN.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test GREATER_THAN_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, b, true",
            "b, a, false",
            "1, 2, true",
            "2, 1, false",
            "2, 10, true",
            "10, 2, false",
            "a, a, true",
            "1, 1, true",
    })
    public void testGreaterThanEqualOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.GREATER_THAN_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName("test LESS_THAN_EQUAL operator")
    @ParameterizedTest(name = "{index} => expected={0}, actual={1}, result={2}")
    @CsvSource({
            "a, b, false",
            "b, a, true",
            "1, 2, false",
            "2, 1, true",
            "2, 10, false",
            "10, 2, true",
            "a, a, true",
            "1, 1, true",
    })
    public void testLessThanEqualOperator(String expected, String actual, String result) {
        var expectedResult = Boolean.parseBoolean(result);
        var actualResult = RuleOperator.LESS_THAN_EQUAL.matches(expected, actual);
        assertEquals(expectedResult, actualResult);
    }
}