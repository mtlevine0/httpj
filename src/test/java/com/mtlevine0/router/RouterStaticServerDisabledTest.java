package com.mtlevine0.router;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.request.HttpMethod;

import com.mtlevine0.response.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class RouterStaticServerDisabledTest extends RouterTest {

    @Before
    public void setup() {
        basePath = "../httpj";
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        router = new Router(basePath);
        router.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldExecuteRequestHandler_WhenHttpRequestMatchesRegisteredRoute() throws IOException {
        assertEquals(getBasicHttpResponse("Custom Body!"), router.route(getBasicGetRequest("/test"),
                HttpResponse.builder().build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldThrowIllegalArgumentException_WhenRegisteringCustomRequestHandlerToWildCardPathRoute() {
        router.registerRoute("*", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldReturn200_WhenCustomRouteRegisteredAndRequestIsHead() throws IOException {
        assertEquals(getBasicHttpResponse(null), router.route(getBasicRequest(HttpMethod.HEAD,"/test"),
                HttpResponse.builder().build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldThrowIllegalArgumentException_WhenRegisteringRequestHandlerWithDuplicateRoute() {
        router.registerRoute("/test", HttpMethod.GET, new CustomHandler());
        router.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldExecuteRequestHandler_WhenRequestHandlerRegisteredToWildCardRouteAndHttpRequestMatchesRoute() throws IOException {
        router.registerRoute("*", HttpMethod.GET, new DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), router.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

    @Test
    public void ShouldExecuteWildCardPathCustomRequestHandler_WhenRequestPathDoesNotMatchStaticPathRoutes() throws IOException {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        router.registerRoute("*", HttpMethod.GET, new DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), router.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

    @Test(expected = MethodNotAllowedException.class)
    public void ShouldThrowMethodNotAllowedException_WhenRequestDoesNotMatchRoute() throws IOException {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        router.route(getBasicGetRequest("/does-not-exist"), HttpResponse.builder().build());
    }

}
