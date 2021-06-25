package com.mtlevine0;

import com.mtlevine0.exception.MethodNotImplementedException;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.resource.DirectoryUtil;
import com.mtlevine0.resource.ResourceUtil;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
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
            handleResponse(httpStatus, body);
            close();
        }
    }

    private void handleRequest(HttpRequest request) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        LOGGER.info(socket.getInetAddress() + " - " + request.getMethod().toString() + " - " + request.getPath());
        if (request.isGetRequest()) {
            handleGetRequest(request);
        } else if (request.isHeadRequest()) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        }
    }

    private void handleGetRequest(HttpRequest request) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        httpStatus = HttpStatus.OK;
        if (ResourceUtil.isDirectory(BASE_PATH + request.getPath())) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.DIRECTORY_LISTING)) {
                body = DirectoryUtil.lsDir(BASE_PATH, request.getPath()).getBytes();
            } else {
                throw new NoSuchFileException("File Does Not Exist: " + request.getPath());
            }
        } else {
            body = ResourceUtil.loadResource(BASE_PATH + request.getPath());
        }
    }

    private void handleResponse(HttpStatus status, byte[] body) {
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

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
