package com.mtlevine0;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            PrintWriter out;
            BufferedReader in;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            String body = "";
            while(in.ready()) {
                body += (char) in.read();
            }

            LOGGER.info(body);

            String resBody = "<html>\n" +
                    "<body>\n" +
                    "<h1>Hello, World!</h1>\n" +
                    "</body>\n" +
                    "</html>";

            String response = "HTTP/1.1 200 OK\n" +
                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                    "Server: Apache/2.2.14 (Win32)\n" +
                    "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                    "Content-Length: " + (resBody.length() + 1) + "\n" +
                    "Content-Type: text/html\n" +
                    "Connection: Closed\n\n";

            out.println(response + resBody);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {

        }
    }
}
