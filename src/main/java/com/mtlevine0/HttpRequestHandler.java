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

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
            HttpRequest request = new HttpRequest(in);
            respondFound(out, request);
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getClass().equals(NoSuchFileException.class)) {
                respondNotFound(out);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
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

    private void respondFound(OutputStream out, HttpRequest request) throws URISyntaxException, IOException {
        String sanitizedPath = new URI(request.getPath()).normalize().getPath();
        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources" + sanitizedPath));
        HttpResponse httpResponse = HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(bytes)
                .build();
        out.write(httpResponse.getResponse());
    }

    private void respondNotFound(OutputStream out) {
        HttpResponse res = HttpResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .body("Not Found".getBytes())
                .build();
        try {
            out.write(res.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
