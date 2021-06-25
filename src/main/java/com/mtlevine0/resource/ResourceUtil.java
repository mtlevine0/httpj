package com.mtlevine0.resource;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class ResourceUtil {

    public static byte[] loadResource(String path) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        byte[] resource = null;
        try {
            resource = Files.readAllBytes(Paths.get(sanitizePath(path)));
        } catch (NoSuchFileException | URISyntaxException | AccessDeniedException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resource;
    }

    public static boolean isDirectory(String path) throws URISyntaxException {
        return new File(sanitizePath(path)).isDirectory();
    }

    public static String sanitizePath(String path) throws URISyntaxException {
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.SANITIZE_PATH)) {
            return new URI(path).normalize().getPath();
        }
        return path;
    }

}
