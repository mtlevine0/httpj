package com.mtlevine0;

import com.mtlevine0.exception.HttpRequestParsingException;
import com.mtlevine0.exception.MethodNotImplementedException;
import com.mtlevine0.handler.DefaultRequestHandler;
import com.mtlevine0.handler.RequestHandler;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Logger;

public class RequestDispatcher implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(RequestDispatcher.class.getName());

    private final Socket socket;
    private OutputStream out;
    private InputStream in;
    private RequestHandler requestHandler;

    public RequestDispatcher(Socket socket, String basePath) {
        this.socket = socket;
        requestHandler = new DefaultRequestHandler(basePath);
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) { }
    }

    public RequestDispatcher(Socket socket, String basePath, RequestHandler requestHandler) {
        this(socket, basePath);
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        HttpResponse httpResponse = null;
        try {
            HttpRequest httpRequest = new HttpRequest(in);
            LOGGER.info(httpRequest.toString());
            httpResponse = requestHandler.handleRequest(httpRequest);
        } catch (MethodNotImplementedException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.NOT_IMPLEMENTED);
        } catch (NoSuchFileException e) {
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            httpResponse = generateBasicHttpResponse(httpStatus);
        } catch (AccessDeniedException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.UNAUTHORIZED);
        } catch (HttpRequestParsingException | URISyntaxException e) {
            e.printStackTrace();
            httpResponse = generateBasicHttpResponse(HttpStatus.BAD_REQUEST);
        } finally {
            handleResponse(httpResponse);
            close();
        }
    }

    private HttpResponse generateBasicHttpResponse(HttpStatus httpStatus) {
        HttpResponse httpResponse;
        httpResponse = HttpResponse.builder().status(httpStatus)
                .body(httpStatus.getReason().getBytes()).build();
        return httpResponse;
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
