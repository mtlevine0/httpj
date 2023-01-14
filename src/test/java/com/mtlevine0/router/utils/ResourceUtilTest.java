package com.mtlevine0.router.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import static org.junit.Assert.*;

public class ResourceUtilTest {
    String basePath = "/tmp/httpj/";

    @Test(expected = AccessDeniedException.class)
    public void sanitizePath_ShouldThrowAccessDeniedException_WhenRequestedPathIsAncestorOfBasePath() throws IOException {
        String path = "/test.txt";
        assertEquals(basePath + path, ResourceUtil.sanitizePath(basePath, "../" + path));
    }

    @Test
    public void sanitizePath_ShouldReturnCanonicalPath_WhenRequestedPathIsDescendantOfBasePath() throws IOException {
        String path = "/test.txt";
        assertEquals(new File(basePath + path).getCanonicalPath(), ResourceUtil.sanitizePath(basePath, path));
    }

}
