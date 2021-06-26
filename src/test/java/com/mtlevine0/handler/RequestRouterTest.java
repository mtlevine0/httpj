package com.mtlevine0.handler;

import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;
import org.junit.Before;

import java.util.Objects;

public class RequestRouterTest {

    protected RequestRouter requestRouter;
    protected String basePath;

    @Before
    public void setup() {
        basePath = "../httpj";
        requestRouter = new RequestRouter(basePath);
        requestRouter.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    protected class CustomHandler implements CustomRequestHandler {
        @Override
        public HttpResponse handleRequest(HttpRequest httpRequest) {
            return HttpResponse.builder().body("Custom Body!".getBytes()).status(HttpStatus.OK).build();
        }
    }

    protected class DefaultRequestHandler implements RequestHandler {
        @Override
        public HttpResponse handleRequest(HttpRequest httpRequest) {
            return HttpResponse.builder().body("Default Body!".getBytes()).status(HttpStatus.OK).build();
        }
    }

    protected HttpRequest getBasicRequest(HttpMethod method, String path) {
        HttpRequest request = new HttpRequest();
        request.setPath(path);
        request.setMethod(method);
        return request;
    }

    protected HttpRequest getBasicGetRequest(String path) {
        return getBasicRequest(HttpMethod.GET, path);
    }

    protected HttpResponse getBasicHttpResponse(String body) {
        return HttpResponse.builder().body(Objects.nonNull(body) ? body.getBytes() : null).status(HttpStatus.OK).build();
    }
}
