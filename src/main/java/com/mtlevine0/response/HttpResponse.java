package com.mtlevine0.response;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import lombok.Builder;
import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

@Value
@Builder
public class HttpResponse {
    public static final String HTTP_PROTOCOL_VERSION = "HTTP/1.1";
    private static final String HTTP_NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String GZIP_ENCODING = "gzip";
    
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
        byte[] bodyBytes = gzipBody();
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.GZIP_ENCODING)) {
            headers.put(CONTENT_ENCODING_HEADER, GZIP_ENCODING);
            headers.put(CONTENT_LENGTH_HEADER, String.valueOf(bodyBytes.length));
        } else {
            headers.put(CONTENT_LENGTH_HEADER, String.valueOf(body.length));
        }

        byte[] responseHeader = this.generateResponseHeader().getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(responseHeader);
        if (Objects.nonNull(body)) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.GZIP_ENCODING)) {
                outputStream.write(gzipBody());
            } else {
                outputStream.write(body);
            }
        }
        return outputStream.toByteArray();
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
