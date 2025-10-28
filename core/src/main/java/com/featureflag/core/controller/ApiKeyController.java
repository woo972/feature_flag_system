package com.featureflag.core.controller;

import com.featureflag.core.dto.ApiKeyResponse;
import com.featureflag.core.dto.CreateApiKeyRequest;
import com.featureflag.core.entity.ApiKeyEntity;
import com.featureflag.core.service.ApiKeyService;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createApiKey(@Valid @RequestBody CreateApiKeyRequest request) {
        ApiKeyEntity entity = apiKeyService.createApiKey(
                request.getName(),
                request.getDescription(),
                request.getExpiresAt()
        );
        // Include the full API key only in the creation response
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiKeyResponse.from(entity, true));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getAllApiKeys() {
        List<ApiKeyResponse> responses = apiKeyService.getAllApiKeys().stream()
                .map(entity -> ApiKeyResponse.from(entity, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiKeyResponse> getApiKey(@PathVariable Long id) {
        return apiKeyService.getApiKeyById(id)
                .map(entity -> ResponseEntity.ok(ApiKeyResponse.from(entity, false)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeApiKey(@PathVariable Long id) {
        apiKeyService.revokeApiKey(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiKeyResponse> activateApiKey(@PathVariable Long id) {
        ApiKeyEntity entity = apiKeyService.activateApiKey(id);
        return ResponseEntity.ok(ApiKeyResponse.from(entity, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable Long id) {
        apiKeyService.deleteApiKey(id);
        return ResponseEntity.noContent().build();
    }
}
