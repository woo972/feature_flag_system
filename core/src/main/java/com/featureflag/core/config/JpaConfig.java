package com.featureflag.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.featureflag.core"})
@EntityScan(basePackages = {"com.featureflag.core"})
public class JpaConfig {
}
