package com.featureflag.sdk.client;

import com.featureflag.sdk.cache.FeatureFlagCache;
import com.featureflag.sdk.datasource.FeatureFlagDataSource;
import com.featureflag.sdk.scheduler.FeatureFlagScheduler;
import com.featureflag.sdk.stream.FeatureFlagStreamListener;
import com.featureflag.shared.model.FeatureFlag;
import com.featureflag.shared.model.FeatureFlagStatus;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import static org.junit.jupiter.api.Assertions.*;

class DefaultFeatureFlagClientTest {
    private static final AtomicLong idSequence = new AtomicLong(1);
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

    @Nested
    @DisplayName("initialize")
    class Initialize {
        @Test
        @DisplayName("polling mode schedules cache refresh runnable")
        void initializePollingMode() throws Exception {
            var featureFlag = sampleFlag("polling-" + UUID.randomUUID());
            var dataSource = new RecordingDataSource(Optional.of(List.of(featureFlag)));
            var cache = new RecordingCache();

            sut = DefaultFeatureFlagClient.builder()
                    .source(dataSource)
                    .cache(cache)
                    .updateMode(UpdateMode.POLLING)
                    .build();

            var scheduler = new TestScheduler();
            injectScheduler(sut, scheduler);

            sut.initialize();

            assertEquals(1, scheduler.initializeCount);
            assertNotNull(scheduler.capturedRunnable);

            scheduler.runCaptured();
            assertEquals(1, dataSource.callCount);
            assertTrue(cache.get(featureFlag.getId()).isPresent());
            assertEquals(1, cache.loadCount);
        }

        @Test
        @DisplayName("stream mode loads cache and refreshes entries on updates")
        void initializeStreamMode() throws Exception {
            var featureFlag = sampleFlag("stream-" + UUID.randomUUID());
            var dataSource = new RecordingDataSource(Optional.of(List.of(featureFlag)));
            var cache = new RecordingCache();

            sut = DefaultFeatureFlagClient.builder()
                    .source(dataSource)
                    .cache(cache)
                    .updateMode(UpdateMode.STREAM)
                    .build();

            var listener = new TestStreamListener();
            injectListener(sut, listener);

            sut.initialize();

            assertEquals(1, dataSource.callCount);
            assertEquals(1, cache.loadCount);
            assertTrue(cache.get(featureFlag.getId()).isPresent());
            assertEquals(1, listener.initializeCount);

            listener.emit(featureFlag.getId());

            assertEquals(featureFlag.getId(), cache.lastPutKey);
            assertTrue(cache.lastPutValue.isPresent());
            assertSame(cache.get(featureFlag.getId()).orElseThrow(), cache.lastPutValue.orElseThrow());
            assertEquals(1, dataSource.singleGetCount);
        }
    }

    @Nested
    @DisplayName("evaluation")
    class Evaluation {
        @Test
        @DisplayName("evaluates cached flag state")
        void isEnabledEvaluatesFlag() {
            var cache = new RecordingCache();
            var flag = sampleFlag("enabled-flag");
            cache.store.put(flag.getId(), flag);

            sut = DefaultFeatureFlagClient.builder()
                    .source(new RecordingDataSource(Optional.empty()))
                    .cache(cache)
                    .updateMode(UpdateMode.STREAM)
                    .build();

            var enabled = sut.isEnabled(flag.getId(), Map.of());

            assertTrue(enabled);
        }

        @Test
        @DisplayName("returns false when flag missing")
        void isEnabledReturnsFalseWhenMissing() {
            var cache = new RecordingCache();

            sut = DefaultFeatureFlagClient.builder()
                    .source(new RecordingDataSource(Optional.empty()))
                    .cache(cache)
                    .updateMode(UpdateMode.STREAM)
                    .build();

            var enabled = sut.isEnabled(999L, Map.of());

            assertFalse(enabled);
        }

        @Test
        @DisplayName("delegates to cache")
        void readAllDelegatesToCache() {
            var cache = new RecordingCache();
            var flagOne = sampleFlag("all-1");
            var flagTwo = sampleFlag("all-2");
            cache.store.put(flagOne.getId(), flagOne);
            cache.store.put(flagTwo.getId(), flagTwo);

            sut = DefaultFeatureFlagClient.builder()
                    .source(new RecordingDataSource(Optional.empty()))
                    .cache(cache)
                    .updateMode(UpdateMode.STREAM)
                    .build();

            var result = sut.readAllFeatureFlags();

            assertTrue(result.isPresent());
            assertEquals(2, result.orElseGet(List::of).size());
        }
    }

    private FeatureFlag sampleFlag(String name) {
        return FeatureFlag.builder()
                .id(idSequence.getAndIncrement())
                .name(name)
                .status(FeatureFlagStatus.ON)
                .build();
    }

    private void injectScheduler(DefaultFeatureFlagClient client, FeatureFlagScheduler scheduler) throws Exception {
        Field field = DefaultFeatureFlagClient.class.getDeclaredField("scheduler");
        field.setAccessible(true);
        field.set(client, scheduler);
    }

    private void injectListener(DefaultFeatureFlagClient client, FeatureFlagStreamListener listener) throws Exception {
        Field field = DefaultFeatureFlagClient.class.getDeclaredField("listener");
        field.setAccessible(true);
        field.set(client, listener);
    }

    private static class RecordingDataSource implements FeatureFlagDataSource {
        private Optional<List<FeatureFlag>> toReturn;
        private int callCount;
        private int singleGetCount;
        private final Map<Long, FeatureFlag> byId = new HashMap<>();

        RecordingDataSource(Optional<List<FeatureFlag>> toReturn) {
            this.toReturn = toReturn;
            toReturn.ifPresent(flags -> flags.forEach(flag -> {
                byId.put(flag.getId(), flag);
            }));
        }

        @Override
        public FeatureFlag get(long featureFlagId) {
            singleGetCount++;
            return byId.get(featureFlagId);
        }

        @Override
        public Optional<List<FeatureFlag>> getFeatureFlags() {
            callCount++;
            return toReturn;
        }

        void putFlag(FeatureFlag featureFlag) {
            byId.put(featureFlag.getId(), featureFlag);
        }
    }

    private static class RecordingCache implements FeatureFlagCache {
        private final Map<Long, FeatureFlag> store = new HashMap<>();
        private int loadCount;
        private Long lastPutKey;
        private Optional<FeatureFlag> lastPutValue = Optional.empty();

        @Override
        public void load(Optional<List<FeatureFlag>> featureFlags) {
            loadCount++;
            featureFlags.ifPresent(flags -> {
                store.clear();
                flags.forEach(flag -> store.put(flag.getId(), flag));
            });
        }

        @Override
        public Optional<FeatureFlag> get(long key) {
            return Optional.ofNullable(store.get(key));
        }

        @Override
        public Optional<List<FeatureFlag>> readAll() {
            return Optional.of(new ArrayList<>(store.values()));
        }

        @Override
        public void put(long key, Optional<FeatureFlag> value) {
            lastPutKey = key;
            lastPutValue = value;
            if (value.isPresent()) {
                store.put(key, value.get());
            } else {
                store.remove(key);
            }
        }
    }

    private static class TestScheduler implements FeatureFlagScheduler {
        private int initializeCount;
        private Runnable capturedRunnable;

        @Override
        public void initialize(Runnable runnable) {
            initializeCount++;
            this.capturedRunnable = runnable;
        }

        @Override
        public void close() {
            // no-op for tests
        }

        void runCaptured() {
            if (capturedRunnable != null) {
                capturedRunnable.run();
            }
        }
    }

    private static class TestStreamListener implements FeatureFlagStreamListener {
        private int initializeCount;
        private LongConsumer consumer;

        @Override
        public void initialize(LongConsumer onFeatureFlagUpdated) {
            initializeCount++;
            this.consumer = onFeatureFlagUpdated;
        }

        @Override
        public void close() {
            // no-op for tests
        }

        void emit(long featureFlagId) {
            if (consumer != null) {
                consumer.accept(featureFlagId);
            }
        }
    }
}
