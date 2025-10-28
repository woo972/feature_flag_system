package com.featureflag.core.config;

import com.featureflag.core.security.ApiKeyAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (health checks, etc.)
                        .requestMatchers("/actuator/**").permitAll()

                        // API Key management endpoints - should be protected by admin authentication
                        // For now, we'll allow these to be accessed, but in production you'd add admin auth
                        .requestMatchers("/api/v1/api-keys/**").permitAll()

                        // SDK client endpoints - require API key authentication
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/{flag-id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/evaluate/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/event-stream").authenticated()

                        // Admin endpoints - management operations (will be protected by admin auth in future)
                        .requestMatchers(HttpMethod.POST, "/api/v1/feature-flags").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/feature-flags/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/page").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/cache/refresh").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
