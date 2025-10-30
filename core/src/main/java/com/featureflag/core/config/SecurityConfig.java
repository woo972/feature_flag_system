package com.featureflag.core.config;

import com.featureflag.core.admin.infrastructure.security.JwtAuthenticationFilter;
import com.featureflag.core.apikey.infrastructure.security.ApiKeyAuthenticationFilter;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/v1/admin/auth/**").permitAll()

                        // Admin endpoints - require JWT authentication with ADMIN or SUPER_ADMIN role
                        .requestMatchers("/api/v1/api-keys/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/feature-flags").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/feature-flags/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/page").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/cache/refresh").hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // SDK client endpoints - require API key authentication
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/{flag-id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/evaluate/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/feature-flags/event-stream").authenticated()

                        .anyRequest().authenticated()
                )
                // JWT filter runs first for admin auth, then API key filter for SDK auth
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiKeyAuthenticationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}

