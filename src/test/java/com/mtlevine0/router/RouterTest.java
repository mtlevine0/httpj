package com.mtlevine0.router;

import com.mtlevine0.httpj.request.HttpMethod;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;
import org.junit.Before;

import java.util.Objects;

public class RouterTest {

    protected Router router;
    protected String basePath;

    @Before
    public void setup() {
        basePath = "../httpj";
        router = new Router(basePath);
        router.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    protected class CustomHandler implements CustomRequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpResponse.setBody("Custom Body!".getBytes());
            httpResponse.setStatus(HttpStatus.OK);
        }
    }

    protected class DefaultRequestHandler implements RequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpResponse.setBody("Default Body!".getBytes());
            httpResponse.setStatus(HttpStatus.OK);
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
