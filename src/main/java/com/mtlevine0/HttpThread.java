package com.mtlevine0;

import java.io.*;
import java.net.Socket;
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
        try {
            LOGGER.info("Running thread!");
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            String rawRequest = readRequest(in);
            HttpRequest request = new HttpRequest(rawRequest);
            LOGGER.info(request.toString());

            byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources" + request.getPath()));
            HttpResponse httpResponse = HttpResponse.builder()
                    .status(HttpStatus.OK)
                    .protocolVersion("HTTP/1.1")
                    .body(bytes)
                    .build();

            out.write(httpResponse.getResponse());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
