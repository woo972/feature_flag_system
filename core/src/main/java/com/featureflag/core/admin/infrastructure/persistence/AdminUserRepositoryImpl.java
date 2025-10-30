package com.featureflag.core.admin.infrastructure.persistence;

import com.featureflag.core.admin.domain.model.AdminUser;
import com.featureflag.core.admin.domain.model.AdminUserId;
import com.featureflag.core.admin.domain.model.Username;
import com.featureflag.core.admin.domain.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminUserRepositoryImpl implements AdminUserRepository {
    private final AdminUserJpaRepository jpaRepository;

    @Override
    public AdminUser save(AdminUser adminUser) {
        AdminUserJpaEntity entity;

        if (adminUser.getId() != null) {
            entity = jpaRepository.findById(adminUser.getId().getValue())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Admin user not found: " + adminUser.getId().getValue()));
            entity.updateFromDomain(adminUser);
        } else {
            entity = AdminUserJpaEntity.fromDomain(adminUser);
        }

        AdminUserJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<AdminUser> findById(AdminUserId id) {
        return jpaRepository.findById(id.getValue())
                .map(AdminUserJpaEntity::toDomain);
    }

    @Override
    public Optional<AdminUser> findByUsername(Username username) {
        return jpaRepository.findByUsername(username.getValue())
                .map(AdminUserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpaRepository.existsByUsername(username.getValue());
    }
}
