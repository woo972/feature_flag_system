package com.featureflag.core.admin.domain.repository;

import com.featureflag.core.admin.domain.model.AdminUser;
import com.featureflag.core.admin.domain.model.AdminUserId;
import com.featureflag.core.admin.domain.model.Username;

import java.util.Optional;

/**
 * Domain repository interface for AdminUser aggregate.
 */
public interface AdminUserRepository {
    AdminUser save(AdminUser adminUser);

    Optional<AdminUser> findById(AdminUserId id);

    Optional<AdminUser> findByUsername(Username username);

    boolean existsByUsername(Username username);
}
