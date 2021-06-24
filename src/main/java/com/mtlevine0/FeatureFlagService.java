package com.mtlevine0;

import java.util.LinkedHashMap;
import java.util.Map;

public class FeatureFlagService {
    private Map<FeatureFlag, Boolean> features;

    public FeatureFlagService() {
        features = new LinkedHashMap<>();
    }

    public void enableFeature(FeatureFlag featureFlag) {
        features.put(featureFlag, true);
    }

    public void disableFeature(FeatureFlag featureFlag) {
        features.put(featureFlag, false);
    }

    public boolean isFeatureActive(FeatureFlag featureFlag) {
        return features.containsKey(featureFlag) && features.get(featureFlag);
    }
}
