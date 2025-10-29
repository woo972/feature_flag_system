package com.featureflag.core.apikey.infrastructure.security;

import com.featureflag.core.apikey.application.service.ApiKeyApplicationService;
import com.featureflag.core.apikey.domain.model.ApiKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter for API key authentication.
 * Part of the infrastructure layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyApplicationService apiKeyApplicationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKeyString = request.getHeader(API_KEY_HEADER);

        if (apiKeyString != null && !apiKeyString.isBlank()) {
            try {
                // Find the API key through application service
                Optional<ApiKey> apiKeyOpt = apiKeyApplicationService.findByValue(apiKeyString);

                if (apiKeyOpt.isPresent()) {
                    ApiKey apiKey = apiKeyOpt.get();

                    // Validate through domain model
                    if (apiKey.isValid()) {
                        // Create authentication token
                        ApiKeyAuthenticationToken authentication =
                                new ApiKeyAuthenticationToken(apiKeyString, apiKey.getName());
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Record usage through application service (async/non-blocking)
                        recordUsageAsync(apiKeyString);
                    } else {
                        if (log.isWarnEnabled()) {
                            log.warn("Invalid or expired API key used: {}", apiKey.getName());
                        }
                    }
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn("Unknown API key attempted: {}...",
                                apiKeyString.substring(0, Math.min(8, apiKeyString.length())));
                    }
                }
            } catch (IllegalArgumentException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Malformed API key in request: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void recordUsageAsync(String apiKeyString) {
        // Record usage in a non-blocking way
        // If recording fails, it won't affect the authentication
        try {
            apiKeyApplicationService.validateAndRecordUsage(apiKeyString);
        } catch (RuntimeException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to record API key usage", e);
            }
        }
    }
}
