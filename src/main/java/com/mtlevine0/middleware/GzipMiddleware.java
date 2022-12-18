package com.mtlevine0.middleware;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public class GzipMiddleware implements Middleware {
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String GZIP_ENCODING = "gzip";

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        byte[] body = response.getBody();
        if (Objects.nonNull(body) && isGzip(request.getHeaders())) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(body);
            gzipOutputStream.close();

            body = outputStream.toByteArray();
            response.setBody(body);

            response.getHeaders().put("Content-Encoding", "gzip");
        }
    }

    private boolean isGzip(Map<String, String> httpRequestHeaders) {
        return httpRequestHeaders.containsKey(ACCEPT_ENCODING_HEADER) &&
                httpRequestHeaders.get(ACCEPT_ENCODING_HEADER).contains(GZIP_ENCODING);
    }
}