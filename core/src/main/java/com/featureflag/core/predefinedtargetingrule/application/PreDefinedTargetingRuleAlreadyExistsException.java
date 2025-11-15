package com.featureflag.core.predefinedtargetingrule.application;

public class PreDefinedTargetingRuleAlreadyExistsException extends RuntimeException {
    public PreDefinedTargetingRuleAlreadyExistsException(String name) {
        super("PreDefinedTargetingRule already exists with name: " + name);
    }
}
