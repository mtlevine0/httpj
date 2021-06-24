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
            if (Objects.isNull(body)) {
                body = httpStatus.getReason().getBytes();
            }
            respond(httpStatus, body);
            close();
        }
    }

    private void handleRequest(HttpRequest request) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        LOGGER.info(socket.getInetAddress() + " - " + request.getMethod().toString() + " - " + request.getPath());
        if (request.getMethod().equals(HttpMethod.GET)) {
            httpStatus = HttpStatus.OK;
            if (!isDirectory(request.getPath())) {
                body = loadResource(request.getPath());
            } else {
                body = lsDir(request.getPath()).getBytes();
            }
        } else if (request.getMethod().equals(HttpMethod.HEAD)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lsDir(String path) throws URISyntaxException {
        String sanitizedPath = new URI(path).normalize().getPath();
        List<File> files = Arrays.asList(new File(BASE_PATH + sanitizedPath).listFiles());
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>Name</th><th>Last Modified</th><th>Size</th></tr>");
        for (File file : files) {
            sb.append("<tr>");
            sb.append("<td>" + file.getName());
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

    private boolean isDirectory(String path) throws URISyntaxException {
        String sanitizedPath = new URI(path).normalize().getPath();
        return new File(BASE_PATH + sanitizedPath).isDirectory();
    }

    private byte[] loadResource(String path) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        byte[] resource = null;
        try {
            String sanitizedPath = new URI(path).normalize().getPath();
            resource = Files.readAllBytes(Paths.get(BASE_PATH + sanitizedPath));
        } catch (NoSuchFileException | URISyntaxException |AccessDeniedException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resource;
    }

    private void respond(HttpStatus status, byte[] body) {
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
