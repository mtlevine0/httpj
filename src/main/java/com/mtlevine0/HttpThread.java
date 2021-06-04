package com.mtlevine0;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class HttpThread implements Runnable {
    private static Logger LOGGER = Logger.getLogger(HttpThread.class.getName());

    private Socket socket;

    public HttpThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream out = null;
        InputStream in = null;
        try {
            LOGGER.info("Running thread!");
            out = socket.getOutputStream();
            in = socket.getInputStream();
            String rawRequest = readRequest(in);
            HttpRequest request = new HttpRequest(rawRequest);
            LOGGER.info(request.toString());

            String sanitizedPath = new URI(request.getPath()).normalize().getPath();
            byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources" + sanitizedPath));
            HttpResponse httpResponse = HttpResponse.builder()
                    .status(HttpStatus.OK)
                    .protocolVersion("HTTP/1.1")
                    .body(bytes)
                    .build();

            out.write(httpResponse.getResponse());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readRequest(InputStream in) throws IOException {
        StringBuilder request = new StringBuilder();
        do {
            request.append((char) in.read());
        } while (in.available() > 0);
        return request.toString();
    }
}
