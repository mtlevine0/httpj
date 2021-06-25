package com.mtlevine0.response;

import lombok.Builder;
import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Value
@Builder
public class HttpResponse {
    private String protocolVersion = "HTTP/1.1";
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
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
            int contentLength = 0;
            if (Objects.nonNull(super.body)) {
                contentLength = generateContentLength(super.body);
            }
            super.headers.put("Content-Length", String.valueOf(contentLength));
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
        if (Objects.nonNull(body)) {
            outputStream.write(body);
        }
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
