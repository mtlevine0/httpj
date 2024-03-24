package com.mtlevine0.httpj.common.response;

import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class HttpResponse {
    public static final String HTTP_PROTOCOL_VERSION = "HTTP/1.1";
    public static final String HTTP_NEW_LINE = "\r\n";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.body = body;
        if (Objects.isNull(headers)) {
            this.headers = new LinkedHashMap<>();
        } else {
            this.headers = headers;
        }
    }

    public byte[] getResponse() throws IOException {
        headers.put(CONTENT_LENGTH_HEADER, String.valueOf(body.length));
        byte[] responseHeader = this.generateResponseHeader().getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseHeader);
        outputStream.write(body);
        outputStream.close();
        return outputStream.toByteArray();
    }

    private String generateResponseHeader() {
        StringBuilder response = new StringBuilder();
        response.append(HTTP_PROTOCOL_VERSION + " " + status.getValue() + " " + status.getReason() + HTTP_NEW_LINE);
        for (String key : headers.keySet()) {
            response.append(key + ": " + headers.get(key) + HTTP_NEW_LINE);
        }
        response.append(HTTP_NEW_LINE);
        return response.toString();
    }
}
