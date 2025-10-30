package com.featureflag.core.admin.domain.model;

import java.util.Objects;

/**
 * Value object representing an encrypted password.
 * Passwords are stored encrypted, never in plaintext.
 */
public class Password {
    private final String encryptedValue;

    private Password(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isBlank()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or blank");
        }
        this.encryptedValue = encryptedValue;
    }

    /**
     * Creates a Password from already encrypted value (e.g., from database).
     */
    public static Password fromEncrypted(String encryptedValue) {
        return new Password(encryptedValue);
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Password password = (Password) o;
        return Objects.equals(encryptedValue, password.encryptedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedValue);
    }

    @Override
    public String toString() {
        return "***REDACTED***";
    }
}
