package com.mtlevine0.router.utils;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ResourceUtil {

    public static byte[] loadResource(String basePath, String path) throws IOException {
        byte[] resource = null;
        try {
            resource = Files.readAllBytes(Paths.get(sanitizePath(basePath, path)).normalize());
        } catch (IOException e) {
            throw e;
        }
        return resource;
    }

    public static boolean isDirectory(String basePath, String path) throws IOException {
        return new File(sanitizePath(basePath, path)).isDirectory();
    }

    public static String sanitizePath(String basePath, String path) throws IOException {
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.SANITIZE_PATH)) {
            String canonicalPath = new File(basePath + path).getCanonicalPath();
            String canonicalBasePath = new File(basePath).getCanonicalPath();
            if (isChild(Path.of(canonicalPath), Path.of(canonicalBasePath))) {
                return canonicalPath;
            } else {
                throw new AccessDeniedException(canonicalPath);
            }
        }
        return basePath + path;
    }

    private static boolean isChild(Path child, Path basePath) {
        return child.startsWith(basePath);
    }

}
