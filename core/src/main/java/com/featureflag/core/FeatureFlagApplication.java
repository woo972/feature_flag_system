package com.featureflag.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.featureflag.core"})
public class FeatureFlagApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagApplication.class, args);
    }

}
