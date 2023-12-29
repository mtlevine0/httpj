package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public class GzipMiddleware implements MiddlewareRequestHandler {
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String GZIP_ENCODING = "gzip";

    @Override
    @SneakyThrows
    public Middleware.Status handleRequest(HttpRequest request, HttpResponse response) {
        byte[] body = response.getBody();
        if (Objects.nonNull(body) && isGzipRequest(request) && !isGzipResponse(response)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(body);
            gzipOutputStream.close();
            body = outputStream.toByteArray();
            outputStream.close();
            response.setBody(body);

            response.getHeaders().put(CONTENT_ENCODING_HEADER, GZIP_ENCODING);
        }
        return Middleware.Status.CONTINUE;
    }

    private boolean isGzipResponse(HttpResponse httpResponse) {
        return (httpResponse.getHeaders().containsKey(CONTENT_ENCODING_HEADER));
    }

    private boolean isGzipRequest(HttpRequest request) {
        return request.getHeaders().containsKey(ACCEPT_ENCODING_HEADER) &&
                request.getHeaders().get(ACCEPT_ENCODING_HEADER).contains(GZIP_ENCODING);
    }
}
