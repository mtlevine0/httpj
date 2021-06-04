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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            InputStream in = socket.getInputStream();
            String rawRequest = readRequest(in);
            HttpRequest request = new HttpRequest(rawRequest);
            LOGGER.info(request.toString());

            String resBody = "<html>\r\n" +
                    "<body>\r\n" +
                    "<h1>Hello, World!</h1>\r\n" +
                    "</body>\r\n" +
                    "</html>\r\n\r\n";

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
                    "Server: Apache/2.2.14 (Win32)\r\n" +
                    "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
                    "Content-Length: " + (resBody.length() + 2) + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Connection: Closed\r\n\r\n";

            out.println(response + resBody);
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
