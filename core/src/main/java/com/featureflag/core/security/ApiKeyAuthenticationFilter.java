package com.featureflag.core.security;

import com.featureflag.core.entity.ApiKeyEntity;
import com.featureflag.core.repository.ApiKeyRepository;
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
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null && !apiKey.isBlank()) {
            Optional<ApiKeyEntity> apiKeyEntity = apiKeyRepository.findByApiKey(apiKey);

            if (apiKeyEntity.isPresent()) {
                ApiKeyEntity key = apiKeyEntity.get();
                if (isValidApiKey(key)) {
                    ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(apiKey, key.getName());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    updateLastUsed(key);
                } else {
                    log.warn("Invalid or expired API key used: {}", key.getName());
                }
            } else {
                log.warn("Unknown API key attempted: {}", apiKey.substring(0, Math.min(8, apiKey.length())) + "...");
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidApiKey(ApiKeyEntity key) {
        if (!key.getActive()) {
            return false;
        }

        return !(key.getExpiresAt() != null && LocalDateTime.now().isAfter(key.getExpiresAt()));
    }

    private void updateLastUsed(ApiKeyEntity key) {
        try {
            key.setLastUsedAt(LocalDateTime.now());
            apiKeyRepository.save(key);
        } catch (RuntimeException e) {
            log.error("Failed to update last used timestamp for API key: {}", key.getName(), e);
        }
    }
}
