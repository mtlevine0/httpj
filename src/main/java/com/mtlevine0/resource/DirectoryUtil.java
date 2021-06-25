package com.mtlevine0.resource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class DirectoryUtil {

    public static String lsDir(String basePath, String path) throws URISyntaxException {
        String sanitizedPath = ResourceUtil.sanitizePath(path);
        List<File> files = Arrays.asList(new File(basePath + sanitizedPath).listFiles());
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>Name</th><th>Last Modified</th><th>Size</th></tr>");
        String parentDirectory = generateParentDirectory(sanitizedPath);
        sb.append("<tr><td><a href=" + parentDirectory + ">..</a></td><td></td><td></td></tr>");
        for (File file : files) {
            sb.append("<tr>");
            if (sanitizedPath.equals("/")) {
                sanitizedPath = "";
            }
            sb.append("<td><a href=" + sanitizedPath + "/" + file.getName() + ">" + file.getName() + "</a>");
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

    private static String generateParentDirectory(String path) {
        int endIndex = path.lastIndexOf("/");
        String parentDirectory = "/";
        if (endIndex != 0) {
            parentDirectory = path.substring(0, endIndex);
        }
        return parentDirectory;
    }

}
