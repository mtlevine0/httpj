package com.mtlevine0.resource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectoryUtil {

    public static String lsDir(String basePath, String path) throws IOException {
        basePath = ResourceUtil.sanitizePath(basePath, "");
        String sanitizedPath = ResourceUtil.sanitizePath(basePath, path);
        List<File> files = Arrays.asList(new File(sanitizedPath).listFiles());
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>Name</th><th>Last Modified</th><th>Size</th></tr>");
        String parentDirectory = generateParentDirectory(diffAncestorPaths(basePath, sanitizedPath));
        sb.append("<tr><td><a href=" + parentDirectory + ">..</a></td><td></td><td></td></tr>");
        for (File file : files) {
            sb.append("<tr>");
            if (sanitizedPath.equals("/")) {
                sanitizedPath = "";
            }
            sb.append("<td><a href=" + diffAncestorPaths(basePath, sanitizedPath) + "/" + file.getName() + ">" + file.getName() + "</a>");
            if (file.isDirectory()) {
                sb.append("/");
            }
            sb.append("</td>");
            sb.append("<td>" + Instant.ofEpochMilli(file.lastModified()).toString() + "</td>");
            sb.append("<td>" + FileUtils.byteCountToDisplaySize(file.length()) + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static String diffAncestorPaths(String basePath, String childPath) {
        return childPath.replace(basePath, "");
    }

    private static String generateParentDirectory(String path) {
        Path parent = Path.of(path).getParent();
        if (Objects.isNull(parent)) {
            return path;
        }
        return parent.toString();
    }

}
