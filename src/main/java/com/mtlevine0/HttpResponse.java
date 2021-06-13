package com.mtlevine0;

import lombok.Builder;
import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Value
@Builder
public class HttpResponse {
    String protocolVersion;
    HttpStatus status;
    String reason;
    Map<String, String> headers;
    byte[] body;

    public HttpResponse(String protocolVersion, HttpStatus status, String reason, Map<String, String> headers, byte[] body) {
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.reason = reason;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new CustomHttpResponseBuilder();
    }

    private static class CustomHttpResponseBuilder extends HttpResponseBuilder {
        @Override
        public HttpResponse build() {
            if (super.headers == null) {
                super.headers = new LinkedHashMap<>();
            }
            super.headers.put("Content-Length", String.valueOf(generateContentLength(super.body)));
            return super.build();
        }
    }

    private static int generateContentLength(byte[] body) {
        return body.length;
    }

    public byte[] getResponse() throws IOException {
        byte[] responseHeader = this.generateResponseHeader().getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseHeader);
        outputStream.write(body);
        return outputStream.toByteArray();
    }

    private String generateResponseHeader() {
        StringBuilder response = new StringBuilder();
        response.append(protocolVersion + " " + status.getValue() + " " + status.getReason() + "\r\n");
        for (String key : headers.keySet()) {
            response.append(key + ": " + headers.get(key) + "\r\n");
        }
        response.append("\r\n");
        return response.toString();
    }
}
