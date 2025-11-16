package com.featureflag.admin.service;

import com.featureflag.admin.client.PageResponse;
import com.featureflag.admin.config.CoreApiProperties;
import com.featureflag.admin.dto.CreatePreDefinedTargetingRuleRequest;
import com.featureflag.admin.dto.PreDefinedTargetingRuleResponse;
import com.featureflag.admin.dto.UpdatePreDefinedTargetingRuleRequest;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminPreDefinedTargetingRuleService {
    private final CoreFeatureFlagClient coreFeatureFlagClient;
    private final CoreApiProperties coreApiProperties;

    public PreDefinedTargetingRuleResponse get(long id) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "pre-defined-targeting-rules", String.valueOf(id))
                .build()
                .toUri();

        return coreFeatureFlagClient.getJson(uri, defaultHeaders(),
                PreDefinedTargetingRuleResponse.class);
    }

    public List<PreDefinedTargetingRuleResponse> list() {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "pre-defined-targeting-rules")
                .build()
                .toUri();

        var listType = coreFeatureFlagClient.mapper()
                .getTypeFactory()
                .constructCollectionType(List.class, PreDefinedTargetingRuleResponse.class);

        List<PreDefinedTargetingRuleResponse> response =
                coreFeatureFlagClient.getJson(uri, defaultHeaders(), listType);
        return response != null ? response : Collections.emptyList();
    }

    public Page<PreDefinedTargetingRuleResponse> listPaged(Pageable pageable) {
        URI uri = appendPageableParams(
                baseUriBuilder()
                        .pathSegment("api", "v1", "pre-defined-targeting-rules", "paged"),
                pageable)
                .build()
                .toUri();

        var pageType = coreFeatureFlagClient.mapper()
                .getTypeFactory()
                .constructParametricType(PageResponse.class,
                        PreDefinedTargetingRuleResponse.class);

        PageResponse<PreDefinedTargetingRuleResponse> response =
                coreFeatureFlagClient.getJson(uri, defaultHeaders(), pageType);
        if (response == null) {
            return Page.empty(pageable);
        }

        List<PreDefinedTargetingRuleResponse> content =
                response.getContent() != null ? response.getContent() : Collections.emptyList();
        return new PageImpl<>(content, pageable, response.getTotalElements());
    }

    public PreDefinedTargetingRuleResponse create(CreatePreDefinedTargetingRuleRequest request) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "pre-defined-targeting-rules")
                .build()
                .toUri();
        return coreFeatureFlagClient.postJson(uri, defaultHeaders(), request,
                PreDefinedTargetingRuleResponse.class);
    }

    public PreDefinedTargetingRuleResponse update(long id,
                                                   UpdatePreDefinedTargetingRuleRequest request) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "pre-defined-targeting-rules", String.valueOf(id))
                .build()
                .toUri();
        return coreFeatureFlagClient.postJson(uri, defaultHeaders(), request,
                PreDefinedTargetingRuleResponse.class);
    }

    public void delete(long id) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "pre-defined-targeting-rules", String.valueOf(id))
                .build()
                .toUri();
        coreFeatureFlagClient.postJson(uri, defaultHeaders(), null, Void.class);
    }

    private UriComponentsBuilder baseUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl(coreApiProperties.getBaseUrl());
    }

    private UriComponentsBuilder appendPageableParams(UriComponentsBuilder builder,
                                                       Pageable pageable) {
        builder.queryParam("page", pageable.getPageNumber());
        builder.queryParam("size", pageable.getPageSize());
        pageable.getSort().forEach(order ->
                builder.queryParam("sort", order.getProperty() + "," + order.getDirection()));
        return builder;
    }

    private Map<String, String> defaultHeaders() {
        return Map.of();
    }
}
