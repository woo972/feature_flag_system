package com.featureflag.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = {"com.featureflag.core.repository"})
@EntityScan(basePackages = {"com.featureflag.core.entity"})
public class FeatureFlagApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagApplication.class, args);
    }
}
