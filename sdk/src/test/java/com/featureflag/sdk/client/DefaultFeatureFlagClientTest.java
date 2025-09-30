package com.featureflag.sdk.client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultFeatureFlagClientTest {
    DefaultFeatureFlagClient sut;

    @DisplayName("create default feature flag client")
    @Nested
    class CreateDefaultFeatureFlagClient {
        @DisplayName("create default feature flag client with default parameters")
        @Test
        void create_with_input_data_source_and_cache() {
            sut = DefaultFeatureFlagClient.builder().build();

            var activatedParameters = sut.activatedParameters();
            assertAll(() -> {
                    assertEquals("DefaultFeatureFlagHttpDataSource", activatedParameters.get("source"));
                    assertEquals("DefaultFeatureFlagLocalCache", activatedParameters.get("cache"));
                    assertEquals("STREAM", activatedParameters.get("updateMode"));
                    assertEquals("null", activatedParameters.get("scheduler"));
                    assertEquals("DefaultFeatureFlagStreamListener", activatedParameters.get("listener"));
                }
            );
        }

        @DisplayName("create default feature flag client with polling update mode")
        @Test
        void create_with_polling_update_mode() {
            sut = DefaultFeatureFlagClient.builder().updateMode(UpdateMode.POLLING).build();

            var activatedParameters = sut.activatedParameters();
            assertAll(() -> {
                        assertEquals("DefaultFeatureFlagHttpDataSource", activatedParameters.get("source"));
                        assertEquals("DefaultFeatureFlagLocalCache", activatedParameters.get("cache"));
                        assertEquals("POLLING", activatedParameters.get("updateMode"));
                        assertEquals("DefaultFeatureFlagScheduler", activatedParameters.get("scheduler"));
                        assertEquals("null", activatedParameters.get("listener"));
                    }
            );
        }
    }
}