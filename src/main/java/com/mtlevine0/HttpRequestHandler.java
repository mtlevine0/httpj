package com.mtlevine0;

import com.mtlevine0.exception.MethodNotImplementedException;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class HttpRequestHandler implements Runnable {
    private static Logger LOGGER = Logger.getLogger(HttpRequestHandler.class.getName());

    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private HttpStatus httpStatus;
    private byte[] body;

    private static final String BASE_PATH = "../httpj";

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) { }
    }

    @Override
    public void run() {
        try {
            HttpRequest httpRequest = new HttpRequest(in);
            LOGGER.info(httpRequest.toString());
            handleRequest(httpRequest);
        } catch (MethodNotImplementedException e) {
            httpStatus = HttpStatus.NOT_IMPLEMENTED;
        } catch (NoSuchFileException e) {
            httpStatus = HttpStatus.NOT_FOUND;
        } catch (AccessDeniedException e) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } catch (URISyntaxException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            respond(httpStatus, body);
            close();
        }
    }

    private void handleRequest(HttpRequest request) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        LOGGER.info(socket.getInetAddress() + " - " + request.getMethod().toString() + " - " + request.getPath());
        if (isGetRequest(request)) {
            handleGetRequest(request);
        } else if (isHeadRequest(request)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        }
    }

    private void handleGetRequest(HttpRequest request) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        httpStatus = HttpStatus.OK;
        if (isDirectory(request.getPath())) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.DIRECTORY_LISTING)) {
                body = lsDir(request.getPath()).getBytes();
            } else {
                throw new NoSuchFileException("File Does Not Exist: " + request.getPath());
            }
        } else {
            body = loadResource(request.getPath());
        }
    }

    private boolean isHeadRequest(HttpRequest request) {
        return request.getMethod().equals(HttpMethod.HEAD);
    }

    private boolean isGetRequest(HttpRequest request) {
        return request.getMethod().equals(HttpMethod.GET);
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lsDir(String path) throws URISyntaxException {
        String sanitizedPath = sanitizePath(path);
        List<File> files = Arrays.asList(new File(BASE_PATH + sanitizedPath).listFiles());
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

    private String generateParentDirectory(String path) {
        int endIndex = path.lastIndexOf("/");
        String parentDirectory = "/";
        if (endIndex != 0) {
            parentDirectory = path.substring(0, endIndex);
        }
        return parentDirectory;
    }

    private boolean isDirectory(String path) throws URISyntaxException {
        return new File(BASE_PATH + sanitizePath(path)).isDirectory();
    }

    private byte[] loadResource(String path) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        byte[] resource = null;
        try {
            resource = Files.readAllBytes(Paths.get(BASE_PATH + sanitizePath(path)));
        } catch (NoSuchFileException | URISyntaxException |AccessDeniedException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resource;
    }

    private String sanitizePath(String path) throws URISyntaxException {
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.SANITIZE_PATH)) {
            return new URI(path).normalize().getPath();
        }
        return path;
    }

    private void respond(HttpStatus status, byte[] body) {
        if (Objects.isNull(body)) {
            body = status.getReason().getBytes();
        }
        try {
            HttpResponse httpResponse = HttpResponse.builder()
                    .status(status)
                    .body(body)
                    .build();
            out.write(httpResponse.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
