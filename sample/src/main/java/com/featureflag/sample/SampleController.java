package com.featureflag.sample;

import com.featureflag.sdk.api.*;
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
            @RequestParam("flag") String flag,
            @RequestParam("key") String key,
            @RequestParam("value") String value) {
        return String.valueOf(flagTest(flag, key, value));
    }

    public boolean flagTest(String flag, String key, String value) {
        return featureFlagClient.evaluate(flag, Map.of(key, value));
    }
}
