package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class MiddlewareHandlerTest {

    MiddlewareHandler middlewareHandler;
    @Before
    public void setup() {
        middlewareHandler = new MiddlewareHandler();
    }

    @Test
    public void givenPreRequestMiddlewareThatExits_whenHandleRequest_thenDoNotExecuteRequestHandler() {
        MiddlewareRequestHandler mockMiddlewareRequestHandler = generateMockMiddlewareRequestHandler(Middleware.Status.EXIT);
        middlewareHandler.registerPreRequestMiddlewareHandler(mockMiddlewareRequestHandler);
        RequestHandler requestHandlerSpy = Mockito.mock(RequestHandler.class);

        middlewareHandler.handleRequest(generateHttpRequest(), generateEmptyHttpResponse(), requestHandlerSpy);

        Mockito.verify(mockMiddlewareRequestHandler, Mockito.times(1)).handleRequest(Mockito.any(), Mockito.any());
        Mockito.verify(requestHandlerSpy, Mockito.times(0)).handleRequest(Mockito.any(), Mockito.any());
    }

    @Test
    public void givenPreRequestMiddlewareThatContinues_whenHandleRequest_thenExecuteRequestHandler() {
        MiddlewareRequestHandler mockMiddlewareRequestHandler = generateMockMiddlewareRequestHandler(Middleware.Status.CONTINUE);
        middlewareHandler.registerPreRequestMiddlewareHandler(mockMiddlewareRequestHandler);
        RequestHandler requestHandlerSpy = Mockito.mock(RequestHandler.class);

        middlewareHandler.handleRequest(generateHttpRequest(), generateEmptyHttpResponse(), requestHandlerSpy);

        Mockito.verify(mockMiddlewareRequestHandler, Mockito.times(1)).handleRequest(Mockito.any(), Mockito.any());
        Mockito.verify(requestHandlerSpy, Mockito.times(1)).handleRequest(Mockito.any(), Mockito.any());
    }

    @Test
    public void givenPostRequestMiddlewareThatExits_whenHandleRequest_thenExecute() {
        MiddlewareRequestHandler mockMiddlewareRequestHandler = generateMockMiddlewareRequestHandler(Middleware.Status.EXIT);
        middlewareHandler.registerPostRequestMiddlewareHandler(mockMiddlewareRequestHandler);
        RequestHandler requestHandlerSpy = Mockito.mock(RequestHandler.class);

        middlewareHandler.handleRequest(generateHttpRequest(), generateEmptyHttpResponse(), requestHandlerSpy);

        Mockito.verify(mockMiddlewareRequestHandler, Mockito.times(1)).handleRequest(Mockito.any(), Mockito.any());
        Mockito.verify(requestHandlerSpy, Mockito.times(1)).handleRequest(Mockito.any(), Mockito.any());
    }

    private MiddlewareRequestHandler generateMockMiddlewareRequestHandler(Middleware.Status status) {
        MiddlewareRequestHandler mockMiddlewareRequestHandler = Mockito.mock(MiddlewareRequestHandler.class);
        Mockito.when(mockMiddlewareRequestHandler.handleRequest(Mockito.any(), Mockito.any())).thenReturn(status);
        return mockMiddlewareRequestHandler;
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
