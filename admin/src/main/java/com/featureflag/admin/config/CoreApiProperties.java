package com.featureflag.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "core.api")
public class CoreApiProperties {
    /**
     * Base URL for the Core service (e.g. http://localhost:8082).
     */
    private String baseUrl;
}
