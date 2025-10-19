package com.featureflag.sample;

import com.featureflag.sdk.client.FeatureFlagClient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/sample")
public class SampleController {
    private final FeatureFlagClient featureFlagClient;

    @GetMapping("/check")
    public String hello(
            @RequestParam("flagId") @Positive long flagId,
            @RequestParam("key") @NotBlank String key,
            @RequestParam("value") @NotBlank String value) {
        return String.valueOf(flagTest(flagId, key, value));
    }

    @GetMapping("/init")
    public void init(){
        log.info("flag init");
        featureFlagClient.initialize();
    }

    public boolean flagTest(@Positive long flagId, @NotBlank String key, @NotBlank String value) {
        log.info("flag test");
        return featureFlagClient.isEnabled(flagId, Map.of(key, value));
    }
}
