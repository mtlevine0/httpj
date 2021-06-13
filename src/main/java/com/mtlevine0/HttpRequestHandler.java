package com.mtlevine0;

import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
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
            if (e.getClass().getName().equalsIgnoreCase("java.nio.file.NoSuchFileException")) {
                LOGGER.warning("not found");
                HttpResponse res = HttpResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .protocolVersion("HTTP/1.1")
                        .body("Not Found".getBytes())
                        .build();
                try {
                out.write(res.getResponse());

                } catch (IOException ex) {
                    LOGGER.warning("You made it here.");
                }
            }
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
                stopWatch.stop();
                LOGGER.info(String.valueOf(stopWatch.getTime()));
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
