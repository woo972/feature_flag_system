package com.featureflag.core.predefinedtargetingrule.application;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.core.predefinedtargetingrule.domain.repository.PreDefinedTargetingRuleRepository;
import com.featureflag.shared.model.RuleOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PreDefinedTargetingRuleService {
    private final PreDefinedTargetingRuleRepository repository;

    @Transactional
    public PreDefinedTargetingRule create(String name, String description, RuleOperator operator, List<String> values) {
        if (repository.findByName(name).isPresent()) {
            throw new PreDefinedTargetingRuleAlreadyExistsException(name);
        }
        var rule = PreDefinedTargetingRule.create(name, description, operator, values);
        return repository.save(rule);
    }

    @Transactional
    public PreDefinedTargetingRule update(long id, String description, RuleOperator operator, List<String> values) {
        var rule = repository.findById(id)
                .orElseThrow(() -> new PreDefinedTargetingRuleNotFoundException(id));
        var updated = rule.update(description, operator, values);
        return repository.save(updated);
    }

    @Transactional(readOnly = true)
    public PreDefinedTargetingRule findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PreDefinedTargetingRuleNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public PreDefinedTargetingRule findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new PreDefinedTargetingRuleNotFoundException(name));
    }

    @Transactional(readOnly = true)
    public List<PreDefinedTargetingRule> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<PreDefinedTargetingRule> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public void delete(long id) {
        if (repository.findById(id).isEmpty()) {
            throw new PreDefinedTargetingRuleNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
