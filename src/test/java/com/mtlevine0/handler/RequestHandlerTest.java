package com.mtlevine0.handler;

import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;
import org.junit.Before;

public class RequestHandlerTest {

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

    protected HttpRequest getBasicGetRequest(String path) {
        HttpRequest request = new HttpRequest();
        request.setPath(path);
        request.setMethod(HttpMethod.GET);
        return request;
    }

    protected HttpResponse getBasicHttpResponse(String body) {
        return HttpResponse.builder().body(body.getBytes()).status(HttpStatus.OK).build();
    }
}
