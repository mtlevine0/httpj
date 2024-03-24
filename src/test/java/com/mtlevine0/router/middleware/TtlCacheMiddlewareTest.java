package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TtlCacheMiddlewareTest {

    @Test
    public void givenNoTtlNoCachedResponse_whenHandleRequest_thenReturnWithContinue() {
        TtlCacheMiddleware ttlCacheMiddleware = new TtlCacheMiddleware();

        Middleware.Status status = ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse());

        assertEquals(status, Middleware.Status.CONTINUE);
    }

    @Test
    public void givenNoTtlCachedResponse_whenHandleRequest_thenReturnWithExit() {
        TtlCacheMiddleware ttlCacheMiddleware = new TtlCacheMiddleware();

        ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse());
        Middleware.Status status = ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse());

        assertEquals(status, Middleware.Status.EXIT);
    }

    @Test
    public void given1SecTtlCachedResponse_whenHandleRequestAfter1100Millis_thenReturnWithContinue() throws InterruptedException {
        List<Middleware.Status> statusList = new ArrayList<>();
        TtlCacheMiddleware ttlCacheMiddleware = new TtlCacheMiddleware(1);

        statusList.add(ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse()));
        statusList.add(ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse()));
        Thread.sleep(1100);
        statusList.add(ttlCacheMiddleware.handleRequest(generateHttpRequest(), generateEmptyHttpResponse()));

        Middleware.Status[] statusArray = new Middleware.Status[] {Middleware.Status.CONTINUE, Middleware.Status.EXIT, Middleware.Status.CONTINUE};
        assertArrayEquals(statusList.toArray(), statusArray);
    }

    private HttpRequest generateHttpRequest() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(HttpMethod.GET);
        httpRequest.setPath("/info");
        Map<String, String> headers = new HashMap<>();
        headers.put("testing", "123");
        httpRequest.setHeaders(headers);
        return httpRequest;
    }

    private HttpResponse generateEmptyHttpResponse() {
        return HttpResponse.builder().build();
    }

}
