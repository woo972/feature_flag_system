package com.featureflag.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FeatureFlagApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagApplication.class, args);
    }
}
