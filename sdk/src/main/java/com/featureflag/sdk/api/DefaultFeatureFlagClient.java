package com.featureflag.sdk.api;


import lombok.*;

import java.util.*;

public class DefaultFeatureFlagClient implements FeatureFlagClient {
    private final FeatureFlagDataSource source;
    private final FeatureFlagCache cache;
    private final FeatureFlagChangeListener listener;

    @Builder
    public DefaultFeatureFlagClient(FeatureFlagDataSource source, FeatureFlagCache cache, FeatureFlagChangeListener listener) {
        this.source = source;
        this.cache = cache;
        this.listener = listener;
    }

    @Override
    public void initialize() {
        // TODO: init 시점과 동시에 flag 변경이 이뤄지면 변경사항이 반영되지 않은 상태가 지속될 것 같다.
        // 1. Zookeeper 등으로 변경을 즉시 전파/반영한다.
        // 2. 주기적으로 전체 flag를 검사한다.
        source.getAll();
    }

    @Override
    public boolean evaluate(String featureFlagName, Map<String, String> criteria) {
        return false;
    }
}
