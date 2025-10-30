package com.featureflag.core.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserJpaRepository extends JpaRepository<AdminUserJpaEntity, Long> {
    Optional<AdminUserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
