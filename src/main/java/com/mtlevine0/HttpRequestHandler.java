package com.mtlevine0;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
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
        try {
            HttpRequest request = new HttpRequest(in);
            byte[] resource = loadResource(request.getPath());
            respond(HttpStatus.OK, resource);
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            respond(HttpStatus.OK, HttpStatus.NOT_FOUND.getReason().getBytes());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            respond(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReason().getBytes());
        } finally {
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

    private byte[] loadResource(String path) throws URISyntaxException, NoSuchFileException {
        byte[] resource = null;
        try {
            String sanitizedPath = new URI(path).normalize().getPath();
            resource = Files.readAllBytes(Paths.get("src/main/resources" + sanitizedPath));
        } catch (NoSuchFileException | URISyntaxException e) {
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
