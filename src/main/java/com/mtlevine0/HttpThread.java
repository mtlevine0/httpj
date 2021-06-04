package com.mtlevine0;

import java.io.*;
import java.net.Socket;
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

            String resBody = "<html>\r\n" +
                    "<body>\r\n" +
                    "<h1>Hello, World!</h1>\r\n" +
                    "</body>\r\n" +
                    "</html>\r\n";

            resBody = "testing123";

            HttpResponse httpResponse = HttpResponse.builder()
                    .status(HttpStatus.OK)
                    .protocolVersion("HTTP/1.1")
                    .body(resBody)
                    .build();

            out.write(httpResponse.toString().getBytes());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {

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
