package com.featureflag.sample;

import com.featureflag.sdk.client.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sample")
public class SampleController {
    private final FeatureFlagClient featureFlagClient;

    @GetMapping("/check")
    public String hello(
            @RequestParam("flagId") long flagId,
            @RequestParam("key") String key,
            @RequestParam("value") String value) {
        return String.valueOf(flagTest(flagId, key, value));
    }

    @GetMapping("/init")
    public void init(){
        log.info("flag init");
        featureFlagClient.initialize();
    }

    public boolean flagTest(long flagId, String key, String value) {
        log.info("flag test");
        return featureFlagClient.isEnabled(flagId, Map.of(key, value));
    }
}
