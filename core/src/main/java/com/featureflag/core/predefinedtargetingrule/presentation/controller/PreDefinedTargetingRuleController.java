package com.featureflag.core.predefinedtargetingrule.presentation.controller;

import com.featureflag.core.predefinedtargetingrule.application.PreDefinedTargetingRuleService;
import com.featureflag.core.predefinedtargetingrule.presentation.dto.CreatePreDefinedTargetingRuleRequest;
import com.featureflag.core.predefinedtargetingrule.presentation.dto.PreDefinedTargetingRuleResponse;
import com.featureflag.core.predefinedtargetingrule.presentation.dto.UpdatePreDefinedTargetingRuleRequest;
import com.featureflag.core.predefinedtargetingrule.presentation.mapper.PreDefinedTargetingRuleResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/pre-defined-targeting-rules")
public class PreDefinedTargetingRuleController {
    private final PreDefinedTargetingRuleService service;
    private final PreDefinedTargetingRuleResponseMapper mapper;

    @PostMapping
    public ResponseEntity<PreDefinedTargetingRuleResponse> create(
            @Valid @RequestBody CreatePreDefinedTargetingRuleRequest request) {
        var rule = service.create(
                request.getName(),
                request.getDescription(),
                request.getOperator(),
                request.getValues()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(rule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreDefinedTargetingRuleResponse> update(
            @PathVariable @Positive long id,
            @Valid @RequestBody UpdatePreDefinedTargetingRuleRequest request) {
        var rule = service.update(
                id,
                request.getDescription(),
                request.getOperator(),
                request.getValues()
        );
        return ResponseEntity.ok(mapper.toResponse(rule));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreDefinedTargetingRuleResponse> getById(@PathVariable @Positive long id) {
        var rule = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(rule));
    }

    @GetMapping
    public ResponseEntity<List<PreDefinedTargetingRuleResponse>> list() {
        var rules = service.findAll();
        var responses = rules.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<PreDefinedTargetingRuleResponse>> listPaged(Pageable pageable) {
        var rules = service.findAll(pageable);
        var responses = rules.map(mapper::toResponse);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
