package com.mtlevine0;

import com.mtlevine0.exception.HttpRequestParsingException;
import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.exception.MethodNotImplementedException;
import com.mtlevine0.handler.RequestRouter;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;
import java.util.logging.Logger;

public class RequestDispatcher implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(RequestDispatcher.class.getName());
    private static final String HTTP_REQUEST_DELIMITER = "\r\n\r\n";

    private final Socket socket;
    private OutputStream out;
    private InputStream in;
    private RequestRouter requestRouter;

    private RequestDispatcher(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) { }
    }

    public RequestDispatcher(Socket socket, String basePath) {
        this(socket);
        this.requestRouter = new RequestRouter(basePath);
    }

    public RequestDispatcher(Socket socket, RequestRouter requestRouter) {
        this(socket, new String());
        this.requestRouter = requestRouter;
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
        HttpResponse httpResponse = null;
        try {
            HttpRequest httpRequest = new HttpRequest(request);
            LOGGER.info(Thread.currentThread().getId() + " - " + httpRequest);
            httpResponse = requestRouter.route(httpRequest);
        } catch (MethodNotImplementedException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.NOT_IMPLEMENTED);
        } catch (MethodNotAllowedException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.METHOD_NOT_ALLOWED);
        } catch (NoSuchFileException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            httpResponse = generateBasicHttpResponse(HttpStatus.UNAUTHORIZED);
        } catch (HttpRequestParsingException e) {
            e.printStackTrace();
            httpResponse = generateBasicHttpResponse(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse = generateBasicHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            handleResponse(httpResponse);
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
