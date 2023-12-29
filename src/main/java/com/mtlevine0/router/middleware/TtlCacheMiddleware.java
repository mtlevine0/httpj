package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TtlCacheMiddleware implements MiddlewareRequestHandler {
    private static final String LAST_MODIFIED_HEADER = "Last-Modified";
    private HttpResponse httpResponse;
    private int ttlSeconds;
    private ZonedDateTime cachedAt;
    private ZonedDateTime cacheExpiresAt;

    public TtlCacheMiddleware() {
        this.ttlSeconds = -1;
    }

    public TtlCacheMiddleware(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        this.setCacheExpiresAt();
    }

    @Override
    public Middleware.Status handleRequest(HttpRequest request, HttpResponse response) {
        Middleware.Status status;
        if (Objects.isNull(httpResponse) || (isCacheExpired() && isCacheTtlEnabled())) {
            this.httpResponse = response;
            this.cachedAt = ZonedDateTime.now();
            this.setCacheExpiresAt();
            status = Middleware.Status.CONTINUE;
        } else {
            response.setStatus(httpResponse.getStatus());
            response.setHeaders(httpResponse.getHeaders());
            response.setBody(httpResponse.getBody());
            status = Middleware.Status.EXIT;
        }
        response.getHeaders().put(LAST_MODIFIED_HEADER, DateTimeFormatter.RFC_1123_DATE_TIME.format(this.cachedAt));
        return status;
    }

    private boolean isCacheExpired() {
        return ZonedDateTime.now().isAfter(this.cacheExpiresAt);
    }

    private boolean isCacheTtlEnabled() {
        return this.ttlSeconds > -1;
    }

    private void setCacheExpiresAt() {
        this.cacheExpiresAt = ZonedDateTime.now().plus(ttlSeconds, ChronoUnit.SECONDS);
    }
}
