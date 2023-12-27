package com.mtlevine0.router;

import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.router.exception.RouteConflictException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdvancedRouterTest {

    protected AdvancedRouter router;

    @Before
    public void setup() {
        router = new AdvancedRouter();
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

    // Given a router with two registered routes and a child router
    // When I route a HttpRequest that matches the child router
    // Then the router should route the request to the child router
    @Test
    public void test() {
        HttpRequest httpRequest = generateHttpRequest();
        HttpResponse httpResponse = generateEmptyHttpResponse();

        router.registerRoute(new Route("/info", HttpMethod.GET, ((req, res) -> {
            res.setBody("info".getBytes());
        })));

        AdvancedRouter childRouter = new AdvancedRouter();
        childRouter.registerRoute(new Route("/", HttpMethod.GET, ((req, res) -> {
            res.setBody("cats".getBytes());
        })));
        router.registerRoute(new Route("/api/v1/cats", childRouter));

        httpRequest.setPath("/api/v1/cats/");
        router.handleRequest(httpRequest, httpResponse);

        System.out.println(new String(httpResponse.getBody()));

    }

    private HttpRequest generateHttpRequest() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(HttpMethod.GET);
        httpRequest.setPath("/info");
        return httpRequest;
    }

    private HttpResponse generateEmptyHttpResponse() {
        return HttpResponse.builder().build();
    }

}
