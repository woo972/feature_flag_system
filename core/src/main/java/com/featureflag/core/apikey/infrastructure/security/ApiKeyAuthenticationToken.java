package com.featureflag.core.apikey.infrastructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

/**
 * Authentication token for API key authentication.
 * Part of the infrastructure layer.
 */
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;

    private final String apiKey;
    private final String principal;

    public ApiKeyAuthenticationToken(String apiKey, String principal) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_SDK_CLIENT")));
        this.apiKey = apiKey;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
