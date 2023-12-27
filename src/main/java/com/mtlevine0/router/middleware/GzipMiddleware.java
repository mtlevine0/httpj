package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public class GzipMiddleware implements Middleware {
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String GZIP_ENCODING = "gzip";

    @Override
    @SneakyThrows
    public void handleRequest(HttpRequest request, HttpResponse response) {
        byte[] body = response.getBody();
        if (Objects.nonNull(body) && isGzip(request.getHeaders())) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(body);
            gzipOutputStream.close();

            body = outputStream.toByteArray();
            response.setBody(body);

            response.getHeaders().put(CONTENT_ENCODING_HEADER, GZIP_ENCODING);
        }
    }

    private boolean isGzip(Map<String, String> httpRequestHeaders) {
        return httpRequestHeaders.containsKey(ACCEPT_ENCODING_HEADER) &&
                httpRequestHeaders.get(ACCEPT_ENCODING_HEADER).contains(GZIP_ENCODING);
    }
}
