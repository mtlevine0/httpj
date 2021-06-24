package com.mtlevine0;

import com.mtlevine0.exception.MethodNotImplementedException;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class HttpRequestHandler implements Runnable {
    private static Logger LOGGER = Logger.getLogger(HttpRequestHandler.class.getName());

    private Socket socket;
    private OutputStream out;
    private InputStream in;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        HttpStatus httpStatus = null;
        byte[] body = null;
        try {
            HttpRequest request = new HttpRequest(in);
            if (request.getMethod().equals(HttpMethod.GET)) {
                body = loadResource(request.getPath());
                httpStatus = HttpStatus.OK;
            } else if (request.getMethod().equals(HttpMethod.HEAD)) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
            }
        } catch (MethodNotImplementedException e) {
            httpStatus = HttpStatus.NOT_IMPLEMENTED;
            e.printStackTrace();
        } catch (NoSuchFileException e) {
            httpStatus = HttpStatus.NOT_FOUND;
            e.printStackTrace();
        } catch (AccessDeniedException e) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            e.printStackTrace();
        } catch (URISyntaxException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        } finally {
            respond(httpStatus, body != null ? body : httpStatus.getReason().getBytes());
            close();
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] loadResource(String path) throws URISyntaxException, NoSuchFileException, AccessDeniedException {
        byte[] resource = null;
        try {
            String sanitizedPath = new URI(path).normalize().getPath();
            resource = Files.readAllBytes(Paths.get("src/main/resources" + sanitizedPath));
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
