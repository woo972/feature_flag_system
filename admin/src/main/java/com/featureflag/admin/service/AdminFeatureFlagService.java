package com.featureflag.admin.service;

import com.featureflag.admin.client.PageResponse;
import com.featureflag.admin.config.CoreApiProperties;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.http.CoreFeatureFlagClient;
import com.featureflag.shared.model.FeatureFlag;
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
public class AdminFeatureFlagService {
    private final CoreFeatureFlagClient coreFeatureFlagClient;
    private final CoreApiProperties coreApiProperties;

    public FeatureFlag get(long id) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "feature-flags", String.valueOf(id))
                .build()
                .toUri();

        return coreFeatureFlagClient.getJson(uri, defaultHeaders(), FeatureFlag.class);
    }

    public Page<FeatureFlag> list(Pageable pageable) {
        URI uri = appendPageableParams(
                baseUriBuilder().pathSegment("api", "v1", "feature-flags", "page"),
                pageable)
                .build()
                .toUri();

        var pageType = coreFeatureFlagClient.mapper()
                .getTypeFactory()
                .constructParametricType(PageResponse.class, FeatureFlag.class);

        PageResponse<FeatureFlag> response = coreFeatureFlagClient.getJson(uri, defaultHeaders(), pageType);
        if (response == null) {
            return Page.empty(pageable);
        }

        List<FeatureFlag> content = response.getContent() != null ? response.getContent() : Collections.emptyList();
        return new PageImpl<>(content, pageable, response.getTotalElements());
    }

    public void register(RegisterFeatureFlagRequest request) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "feature-flags")
                .build()
                .toUri();
        coreFeatureFlagClient.postJson(uri, defaultHeaders(), request, FeatureFlag.class);
    }

    public FeatureFlag on(long id) {
        return toggle(id, "on");
    }

    public FeatureFlag off(long id) {
        return toggle(id, "off");
    }

    public FeatureFlag archive(long id) {
        return toggle(id, "archive");
    }

    private FeatureFlag toggle(long id, String action) {
        URI uri = baseUriBuilder()
                .pathSegment("api", "v1", "feature-flags", String.valueOf(id), action)
                .build()
                .toUri();
        return coreFeatureFlagClient.postJson(uri, defaultHeaders(), null, FeatureFlag.class);
    }

    private UriComponentsBuilder baseUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl(coreApiProperties.getBaseUrl());
    }

    private UriComponentsBuilder appendPageableParams(UriComponentsBuilder builder, Pageable pageable) {
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
