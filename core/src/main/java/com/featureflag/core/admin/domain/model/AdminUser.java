package com.featureflag.core.admin.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an admin user.
 * Rich domain model with business logic.
 */
public class AdminUser {
    private final AdminUserId id;
    private final Username username;
    private Password password;
    private final String email;
    private AdminRole role;
    private boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private AdminUser(Builder builder) {
        if (builder.email != null && !isValidEmail(builder.email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.id = builder.id;
        this.username = Objects.requireNonNull(builder.username, "Username cannot be null");
        this.password = Objects.requireNonNull(builder.password, "Password cannot be null");
        this.email = builder.email;
        this.role = builder.role != null ? builder.role : AdminRole.ADMIN;
        this.enabled = builder.enabled != null ? builder.enabled : true;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastLoginAt = builder.lastLoginAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new admin user with encrypted password.
     */
    public static AdminUser create(Username username, Password password, String email, AdminRole role) {
        return builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }

    /**
     * Records a successful login.
     */
    public void recordLogin() {
        if (!enabled) {
            throw new IllegalStateException("Cannot record login for disabled user");
        }
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Disables this admin user.
     */
    public void disable() {
        if (!enabled) {
            throw new IllegalStateException("User is already disabled");
        }
        this.enabled = false;
    }

    /**
     * Enables this admin user.
     */
    public void enable() {
        if (enabled) {
            throw new IllegalStateException("User is already enabled");
        }
        this.enabled = true;
    }

    /**
     * Changes the password.
     */
    public void changePassword(Password newPassword) {
        this.password = Objects.requireNonNull(newPassword, "New password cannot be null");
    }

    /**
     * Changes the role.
     */
    public void changeRole(AdminRole newRole) {
        this.role = Objects.requireNonNull(newRole, "New role cannot be null");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // Getters
    public AdminUserId getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public Password getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public AdminRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminUser adminUser = (AdminUser) o;
        return Objects.equals(id, adminUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private AdminUserId id;
        private Username username;
        private Password password;
        private String email;
        private AdminRole role;
        private Boolean enabled;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;

        public Builder id(AdminUserId id) {
            this.id = id;
            return this;
        }

        public Builder username(Username username) {
            this.username = username;
            return this;
        }

        public Builder password(Password password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(AdminRole role) {
            this.role = role;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public AdminUser build() {
            return new AdminUser(this);
        }
    }
}
