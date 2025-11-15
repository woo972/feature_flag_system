package com.featureflag.core.predefinedtargetingrule.application;

import com.featureflag.core.predefinedtargetingrule.domain.model.PreDefinedTargetingRule;
import com.featureflag.core.predefinedtargetingrule.domain.repository.PreDefinedTargetingRuleRepository;
import com.featureflag.shared.model.RuleOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreDefinedTargetingRuleServiceTest {
    private PreDefinedTargetingRuleService service;
    private PreDefinedTargetingRuleRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(PreDefinedTargetingRuleRepository.class);
        service = new PreDefinedTargetingRuleService(repository);
    }

    @DisplayName("create pre-defined targeting rule successfully")
    @Test
    void createSuccess() {
        var name = "country-rule";
        var description = "Country targeting rule";
        var operator = RuleOperator.IN;
        var values = List.of("US", "UK");

        when(repository.findByName(name)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            var rule = invocation.getArgument(0, PreDefinedTargetingRule.class);
            return new PreDefinedTargetingRule(
                    1L,
                    rule.name(),
                    rule.description(),
                    rule.operator(),
                    rule.values(),
                    rule.createdAt(),
                    rule.updatedAt()
            );
        });

        var result = service.create(name, description, operator, values);

        assertNotNull(result.id());
        assertEquals(name, result.name());
        assertEquals(description, result.description());
        assertEquals(operator, result.operator());
        assertEquals(values, result.values());
        verify(repository).findByName(name);
        verify(repository).save(any());
    }

    @DisplayName("create fails when rule with same name already exists")
    @Test
    void createFailsWhenNameExists() {
        var name = "country-rule";
        var existingRule = new PreDefinedTargetingRule(
                1L,
                name,
                "Existing",
                RuleOperator.IN,
                List.of("US"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findByName(name)).thenReturn(Optional.of(existingRule));

        assertThrows(PreDefinedTargetingRuleAlreadyExistsException.class, () -> {
            service.create(name, "New", RuleOperator.EQUAL, List.of("US"));
        });
    }

    @DisplayName("update pre-defined targeting rule successfully")
    @Test
    void updateSuccess() {
        var id = 1L;
        var existingRule = new PreDefinedTargetingRule(
                id,
                "country-rule",
                "Old description",
                RuleOperator.IN,
                List.of("US"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        var newDescription = "Updated description";
        var newOperator = RuleOperator.NOT_IN;
        var newValues = List.of("US", "UK", "CA");

        when(repository.findById(id)).thenReturn(Optional.of(existingRule));
        when(repository.save(any())).thenAnswer(invocation ->
                invocation.getArgument(0, PreDefinedTargetingRule.class));

        var result = service.update(id, newDescription, newOperator, newValues);

        assertEquals(newDescription, result.description());
        assertEquals(newOperator, result.operator());
        assertEquals(newValues, result.values());
        verify(repository).findById(id);
        verify(repository).save(any());
    }

    @DisplayName("update fails when rule not found")
    @Test
    void updateFailsWhenNotFound() {
        var id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PreDefinedTargetingRuleNotFoundException.class, () -> {
            service.update(id, "New", RuleOperator.EQUAL, List.of("US"));
        });
    }

    @DisplayName("findById returns rule when exists")
    @Test
    void findByIdSuccess() {
        var id = 1L;
        var rule = new PreDefinedTargetingRule(
                id,
                "country-rule",
                "Description",
                RuleOperator.IN,
                List.of("US"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findById(id)).thenReturn(Optional.of(rule));

        var result = service.findById(id);

        assertEquals(rule, result);
        verify(repository).findById(id);
    }

    @DisplayName("findById throws exception when not found")
    @Test
    void findByIdNotFound() {
        var id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PreDefinedTargetingRuleNotFoundException.class, () -> {
            service.findById(id);
        });
    }

    @DisplayName("delete removes existing rule")
    @Test
    void deleteSuccess() {
        var id = 1L;
        var rule = new PreDefinedTargetingRule(
                id,
                "country-rule",
                "Description",
                RuleOperator.IN,
                List.of("US"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findById(id)).thenReturn(Optional.of(rule));

        service.delete(id);

        verify(repository).findById(id);
        verify(repository).deleteById(id);
    }

    @DisplayName("delete fails when rule not found")
    @Test
    void deleteFailsWhenNotFound() {
        var id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PreDefinedTargetingRuleNotFoundException.class, () -> {
            service.delete(id);
        });
    }
}
