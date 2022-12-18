package com.mtlevine0.response;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpRequest;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

@Data
@Builder
public class HttpResponse {
    public static final String HTTP_PROTOCOL_VERSION = "HTTP/1.1";
    public static final String HTTP_NEW_LINE = "\r\n";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    public static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    public static final String GZIP_ENCODING = "gzip";
    
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

    public byte[] getResponse(HttpRequest request) throws IOException {
        Map<String, String> httpRequestHeaders = request.getHeaders();
        if (Objects.nonNull(body)) {
            if (isGzip(httpRequestHeaders)) {
                byte[] bodyBytes = gzipBody();
                headers.put(CONTENT_ENCODING_HEADER, GZIP_ENCODING);
                headers.put(CONTENT_LENGTH_HEADER, String.valueOf(bodyBytes.length));
            } else {
                headers.put(CONTENT_LENGTH_HEADER, String.valueOf(body.length));
            }
        }

        byte[] responseHeader = this.generateResponseHeader().getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseHeader);
        if (Objects.nonNull(body)) {
            if (isGzip(httpRequestHeaders)) {
                outputStream.write(gzipBody());
            } else {
                outputStream.write(body);
            }
        }
        return outputStream.toByteArray();
    }

    private boolean isGzip(Map<String, String> httpRequestHeaders) {
        return FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.GZIP_ENCODING) &&
                httpRequestHeaders.containsKey(ACCEPT_ENCODING_HEADER) &&
                httpRequestHeaders.get(ACCEPT_ENCODING_HEADER).contains(GZIP_ENCODING);
    }

    private byte[] gzipBody() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(body);
        gzipOutputStream.close();
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
