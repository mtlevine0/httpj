package com.mtlevine0.httpj;

import java.util.*;

public class FeatureFlagContext {
    private static FeatureFlagContext featureFlagContext;
    private final Map<FeatureFlag, Boolean> features = new LinkedHashMap<>();

    private FeatureFlagContext() {
        List<FeatureFlag> flags = Arrays.asList(FeatureFlag.values());
        for (FeatureFlag flag : flags) {
            features.put(flag, flag.getDefaultState());
        }
    }

    public static FeatureFlagContext getInstance() {
        if (Objects.isNull(featureFlagContext)) {
            featureFlagContext = new FeatureFlagContext();
        }
        return featureFlagContext;
    }

    public Map<FeatureFlag, Boolean> getFeatures() {
        return features;
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
