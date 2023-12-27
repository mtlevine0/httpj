package com.mtlevine0.httpj;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RequestDispatcher implements Runnable {
    private static final String HTTP_REQUEST_DELIMITER = "\r\n\r\n";

    private final Socket socket;
    private OutputStream out;
    private InputStream in;
    private RequestHandler requestHandler;

    private RequestDispatcher(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) { }
    }

    public RequestDispatcher(Socket socket, RequestHandler requestHandler) {
        this(socket);
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(new InputStreamReader(in));
        scanner.useDelimiter(HTTP_REQUEST_DELIMITER);
        while(scanner.hasNext()) {
            dispatch(new ByteArrayInputStream(scanner.next().getBytes()));
        }
        close();
    }

    private void dispatch(InputStream request) {
        HttpResponse httpResponse = HttpResponse.builder().build();
        HttpRequest httpRequest;
        try {
            httpRequest = new HttpRequest(request);
            requestHandler.handleRequest(httpRequest, httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
            generateBasicHttpResponse(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            handleResponse(httpResponse);
        }
    }

    private void generateBasicHttpResponse(HttpResponse httpResponse, HttpStatus httpStatus, String message) {
        httpResponse.setStatus(httpStatus);
        httpResponse.setBody((httpStatus.getReason() + ": " + message).getBytes());
    }

    private void handleResponse(HttpResponse httpResponse) {
        try {
            out.write(httpResponse.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
