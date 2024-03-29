package com.mtlevine0.router;

import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;
import com.mtlevine0.router.exception.RouteConflictException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BasicRouterTest {

    protected BasicRouter router;

    @Before
    public void setup() {
        router = new BasicRouter();
    }

    // Given a router with a single route registered
    // When I route a HttpRequest with method and path that matches the registered route
    // Then the routes RequestHandler should process the HttpRequest
    @Test
    public void handleRequest_whenHandleRequest_thenRequestRoutedAndHandled() {
        HttpRequest httpRequest = generateHttpRequest();
        HttpResponse httpResponse = generateEmptyHttpResponse();
        String resBody = "info handler";

        Route route = new Route("/info", HttpMethod.GET, ((req, res) -> {
            res.setBody(resBody.getBytes());
            res.setStatus(HttpStatus.OK);
        }));
        router.registerRoute(route);

        router.handleRequest(httpRequest, httpResponse);

        assertEquals(resBody, new String(httpResponse.getBody()));
    }

    // Given a router with a single route registered
    // When I register a conflicting route
    // Then the router should raise an exception
    @Test(expected = RouteConflictException.class)
    public void registerRoute_whenRegisterRouteWithConflict_thenThrowRouteConflictException() {
        router.registerRoute(new Route("/test", HttpMethod.GET, null));
        router.registerRoute(new Route("/test", HttpMethod.GET, null));
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
