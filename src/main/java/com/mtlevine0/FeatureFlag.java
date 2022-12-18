package com.mtlevine0;

public enum FeatureFlag {
    DIRECTORY_LISTING(false),
    STATIC_FILE_SERVER(false),
    SANITIZE_PATH(true),
    GZIP_ENCODING(true),
    REQUEST_LOGGING(false);

    private final boolean defaultState;

    FeatureFlag(boolean defaultState) {
        this.defaultState = defaultState;
    }

    public boolean getDefaultState() {
        return this.defaultState;
    }
}
