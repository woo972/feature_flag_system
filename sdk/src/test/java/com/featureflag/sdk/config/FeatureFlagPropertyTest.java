package com.featureflag.sdk.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureFlagPropertyTest {

    @Test
    void testDefaultBaseUrl() {
        assertEquals("http://localhost:8082", FeatureFlagProperty.BASE_URL);
    }
}
