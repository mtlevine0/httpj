package com.mtlevine0.httpj;

import com.mtlevine0.httpj.exception.HttpRequestParsingException;
import com.mtlevine0.httpj.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.exception.MethodNotImplementedException;
import com.mtlevine0.router.Router;
import com.mtlevine0.router.middleware.MiddlewareService;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class RequestDispatcher implements Runnable {
    private static final String HTTP_REQUEST_DELIMITER = "\r\n\r\n";

    private final Socket socket;
    private OutputStream out;
    private InputStream in;
    private Router router;
    private MiddlewareService middlewareService;

    private RequestDispatcher(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) { }
    }

    public RequestDispatcher(Socket socket, String basePath) {
        this(socket);
        this.router = new Router(basePath);
    }

    public RequestDispatcher(Socket socket, Router router) {
        this(socket, new String());
        this.router = router;
        this.middlewareService = new MiddlewareService(router);
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
        HttpRequest httpRequest = null;
        try {
            httpRequest = new HttpRequest(request);
            middlewareService.execute(httpRequest, httpResponse);
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
