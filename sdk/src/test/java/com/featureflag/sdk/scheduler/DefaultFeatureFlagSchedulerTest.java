package com.featureflag.sdk.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultFeatureFlagSchedulerTest {

    private final DefaultFeatureFlagScheduler scheduler = new DefaultFeatureFlagScheduler();

    @AfterEach
    void tearDown() {
        scheduler.close();
    }

    @Test
    @DisplayName("executes scheduled runnable")
    void initializeRunsRunnable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // scheduler에 의해 count가 zero가 된다
        scheduler.initialize(latch::countDown);

        // scheduler의 실행이 timeout 이전에 실행되므로, count가 zero가 되어 await true 반환한다
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("stops scheduler without errors even when called twice")
    void closeIsIdempotent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        scheduler.initialize(latch::countDown);
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        assertDoesNotThrow(scheduler::close);
        assertDoesNotThrow(scheduler::close);
    }
}
