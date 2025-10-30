package com.featureflag.core.admin.infrastructure.persistence;

import com.featureflag.core.admin.domain.model.AdminRole;
import com.featureflag.core.admin.domain.model.AdminUser;
import com.featureflag.core.admin.domain.model.AdminUserId;
import com.featureflag.core.admin.domain.model.Password;
import com.featureflag.core.admin.domain.model.Username;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdminRole role;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastLoginAt;

    public AdminUser toDomain() {
        return AdminUser.builder()
                .id(id != null ? AdminUserId.of(id) : null)
                .username(Username.of(username))
                .password(Password.fromEncrypted(password))
                .email(email)
                .role(role)
                .enabled(enabled)
                .createdAt(createdAt)
                .lastLoginAt(lastLoginAt)
                .build();
    }

    public static AdminUserJpaEntity fromDomain(AdminUser adminUser) {
        return AdminUserJpaEntity.builder()
                .id(adminUser.getId() != null ? adminUser.getId().getValue() : null)
                .username(adminUser.getUsername().getValue())
                .password(adminUser.getPassword().getEncryptedValue())
                .email(adminUser.getEmail())
                .role(adminUser.getRole())
                .enabled(adminUser.isEnabled())
                .createdAt(adminUser.getCreatedAt())
                .lastLoginAt(adminUser.getLastLoginAt())
                .build();
    }

    public void updateFromDomain(AdminUser adminUser) {
        this.username = adminUser.getUsername().getValue();
        this.password = adminUser.getPassword().getEncryptedValue();
        this.email = adminUser.getEmail();
        this.role = adminUser.getRole();
        this.enabled = adminUser.isEnabled();
        this.createdAt = adminUser.getCreatedAt();
        this.lastLoginAt = adminUser.getLastLoginAt();
    }
}
