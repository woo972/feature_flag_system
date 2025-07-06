package com.featureflag.core.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FeatureFlagUpdatedEvent extends ApplicationEvent {

    public FeatureFlagUpdatedEvent(Object source) {
        super(source);
    }
}
