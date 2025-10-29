package com.featureflag.core.apikey.presentation.controller;

import com.featureflag.core.apikey.application.dto.ApiKeyDto;
import com.featureflag.core.apikey.application.dto.CreateApiKeyCommand;
import com.featureflag.core.apikey.application.service.ApiKeyApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeyController {
    private final ApiKeyApplicationService apiKeyApplicationService;

    @PostMapping
    public ResponseEntity<ApiKeyDto> createApiKey(@Valid @RequestBody CreateApiKeyCommand command) {
        ApiKeyDto apiKeyDto = apiKeyApplicationService.createApiKey(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyDto);
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyDto>> getAllApiKeys() {
        List<ApiKeyDto> apiKeys = apiKeyApplicationService.getAllApiKeys();
        return ResponseEntity.ok(apiKeys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiKeyDto> getApiKey(@PathVariable Long id) {
        return apiKeyApplicationService.getApiKeyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeApiKey(@PathVariable Long id) {
        apiKeyApplicationService.revokeApiKey(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiKeyDto> activateApiKey(@PathVariable Long id) {
        ApiKeyDto apiKeyDto = apiKeyApplicationService.activateApiKey(id);
        return ResponseEntity.ok(apiKeyDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable Long id) {
        apiKeyApplicationService.deleteApiKey(id);
        return ResponseEntity.noContent().build();
    }
}
