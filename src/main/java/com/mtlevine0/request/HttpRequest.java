package com.mtlevine0.request;

import com.mtlevine0.exception.MethodNotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
    private String request;
    private HttpMethod method;
    private String path;
    private String protocolVersion;
    private Map<String, String> headers;
    private String body;

    public HttpRequest() {}

    public HttpRequest(InputStream in) {
        this.request = readRequest(in);
        parseRequest();
    }

    private String readRequest(InputStream in) {
        StringBuilder request = new StringBuilder();
        try {
            do {
                request.append((char) in.read());
            } while (in.available() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return request.toString();
    }

    private void parseRequest() {
        String[] requestLines = request.split("\r\n");
        method = parseMethod(requestLines);
        path = parsePath(requestLines);
        protocolVersion = parseProtocolVersion(requestLines);
        headers = parseHeaders(requestLines);
        body = parseBody(requestLines);
    }

    private HttpMethod parseMethod(String[] requestLines) {
        String[] requestComponents = requestLines[0].split(" ");
        HttpMethod method = null;
        try {
            method = HttpMethod.valueOf(requestComponents[0]);
        } catch (IllegalArgumentException e) {
            new MethodNotImplementedException("Method not implemented: " + requestComponents[0]);
        }
        return method;
    }

    private String parsePath(String[] requestLines) {
        String[] requestComponents = requestLines[0].split(" ");
        return requestComponents[1];
    }

    private String parseProtocolVersion(String[] requestLines) {
        String[] requestComponents = requestLines[0].split(" ");
        return requestComponents[2];
    }

    private Map<String, String> parseHeaders(String[] requestLines) {
        int start = 1;
        int length = getNumberOfHeaders(requestLines);
        Map<String, String> headers = new LinkedHashMap<>();

        for (int i = start; i < start + length; i++) {
            String key = requestLines[i].split(": ", 2)[0];
            String value = requestLines[i].split(": ", 2)[1];
            headers.put(key, value);
        }

        return headers;
    }

    private int getNumberOfHeaders(String[] requestLines) {
        int start = 1;
        for (int i = start; i < requestLines.length; i++) {
            if (requestLines[i].isEmpty()) {
                return i - 1;
            }
        }
        return requestLines.length - 1;
    }

    private String parseBody(String[] requestLines) {
        StringBuilder sb = new StringBuilder();
        int headerLength = getNumberOfHeaders(requestLines);

        for (int i = headerLength + 1; i < requestLines.length; i++) {
            sb.append(requestLines[i]);
        }
        return sb.toString();
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method.toString() + " " + path + " " + protocolVersion + "\r\n");
        for (String headerKey : headers.keySet()) {
            sb.append(headerKey + ": " + headers.get(headerKey) + "\r\n");
        }
        sb.append("\r\n");
        sb.append(body);
        sb.append("\r\n\r\n");
        return sb.toString();
    }
}
