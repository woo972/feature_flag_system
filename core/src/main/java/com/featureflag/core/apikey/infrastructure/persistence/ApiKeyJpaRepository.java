package com.featureflag.core.apikey.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyJpaEntity, Long> {
    Optional<ApiKeyJpaEntity> findByApiKey(String apiKey);

    boolean existsByApiKey(String apiKey);
}
