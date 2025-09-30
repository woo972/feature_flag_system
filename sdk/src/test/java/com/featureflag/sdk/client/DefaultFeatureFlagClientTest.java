package com.featureflag.sdk.client;

import com.featureflag.sdk.cache.*;
import com.featureflag.sdk.datasource.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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