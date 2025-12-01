package com.featureflag.core.admin.application.service;

import com.featureflag.core.admin.application.dto.LoginRequest;
import com.featureflag.core.admin.application.dto.LoginResponse;
import com.featureflag.core.admin.domain.model.AdminUser;
import com.featureflag.core.admin.domain.model.Username;
import com.featureflag.core.admin.domain.repository.AdminUserRepository;
import com.featureflag.core.admin.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthenticationService {
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Username username = Username.of(request.getUsername());

        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username.getValue());
                    return new IllegalArgumentException("Invalid credentials");
                });

        if (!adminUser.isEnabled()) {
            log.error("User disabled: {}", username.getValue());
            throw new IllegalStateException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPassword().getEncryptedValue())) {
            log.error("Password mismatch for user: {}", username.getValue());
            throw new IllegalArgumentException("Invalid credentials");
        }

        adminUser.recordLogin();
        adminUserRepository.save(adminUser);

        String token = jwtUtil.generateToken(
                adminUser.getUsername().getValue(),
                adminUser.getRole().name());

        log.info("Admin user logged in: {}", adminUser.getUsername().getValue());

        return LoginResponse.builder()
                .token(token)
                .username(adminUser.getUsername().getValue())
                .role(adminUser.getRole().name())
                .build();
    }
}
