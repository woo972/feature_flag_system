package com.featureflag.core.predefinedtargetingrule.domain.repository;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PreDefinedTargetingRuleRepository {
    PreDefinedTargetingRule save(PreDefinedTargetingRule rule);

    Optional<PreDefinedTargetingRule> findById(long id);

    Optional<PreDefinedTargetingRule> findByName(String name);

    List<PreDefinedTargetingRule> findAll();

    Page<PreDefinedTargetingRule> findAll(Pageable pageable);

    void deleteById(long id);
}
