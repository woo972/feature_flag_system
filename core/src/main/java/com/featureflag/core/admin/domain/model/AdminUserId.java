package com.featureflag.core.admin.domain.model;

import java.util.Objects;

/**
 * Value object representing an admin user identifier.
 */
public class AdminUserId {
    private final Long value;

    private AdminUserId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Admin user ID must be a positive number");
        }
        this.value = value;
    }

    public static AdminUserId of(Long value) {
        return new AdminUserId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminUserId that = (AdminUserId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
