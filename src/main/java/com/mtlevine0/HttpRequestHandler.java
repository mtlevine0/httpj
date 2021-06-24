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
            HttpRequest request = new HttpRequest(in);
            LOGGER.info(request.toString());

            String sanitizedPath = new URI(request.getPath()).normalize().getPath();
            byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources" + sanitizedPath));
            HttpResponse httpResponse = HttpResponse.builder()
                    .status(HttpStatus.OK)
                    .body(bytes)
                    .build();

            out.write(httpResponse.getResponse());

        } catch (IOException e) {
            if (e.getClass().getName().equalsIgnoreCase("java.nio.file.NoSuchFileException")) {
                LOGGER.warning("not found");
                HttpResponse res = HttpResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
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

}
