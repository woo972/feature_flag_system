package com.featureflag.core.predefinedtargetingrule.application;

public class PreDefinedTargetingRuleNotFoundException extends RuntimeException {
    public PreDefinedTargetingRuleNotFoundException(long id) {
        super("PreDefinedTargetingRule not found with id: " + id);
    }

    public PreDefinedTargetingRuleNotFoundException(String name) {
        super("PreDefinedTargetingRule not found with name: " + name);
    }
}
