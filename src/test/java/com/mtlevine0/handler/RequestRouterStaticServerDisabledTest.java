package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.request.HttpMethod;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

public class RequestRouterStaticServerDisabledTest extends RequestRouterTest {

    @Before
    public void setup() {
        basePath = "../httpj";
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        requestRouter = new RequestRouter(basePath);
        requestRouter.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldExecuteRequestHandler_WhenHttpRequestMatchesRegisteredRoute() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        assertEquals(getBasicHttpResponse("Custom Body!"), requestRouter.route(getBasicGetRequest("/test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldThrowIllegalArgumentException_WhenRegisteringCustomRequestHandlerToWildCardPathRoute() {
        requestRouter.registerRoute("*", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldReturn200_WhenCustomRouteRegisteredAndRequestIsHead() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        assertEquals(getBasicHttpResponse(null), requestRouter.route(getBasicRequest(HttpMethod.HEAD,"/test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldThrowIllegalArgumentException_WhenRegisteringRequestHandlerWithDuplicateRoute() {
        requestRouter.registerRoute("/test", HttpMethod.GET, new CustomHandler());
        requestRouter.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test
    public void ShouldExecuteRequestHandler_WhenRequestHandlerRegisteredToWildCardRouteAndHttpRequestMatchesRoute() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        requestRouter.registerRoute("*", HttpMethod.GET, new DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/")));
    }

    @Test
    public void ShouldExecuteWildCardPathCustomRequestHandler_WhenRequestPathDoesNotMatchStaticPathRoutes() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        requestRouter.registerRoute("*", HttpMethod.GET, new DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/")));
    }

    @Test(expected = MethodNotAllowedException.class)
    public void ShouldThrowMethodNotAllowedException_WhenRequestDoesNotMatchRoute() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.STATIC_FILE_SERVER);
        requestRouter.route(getBasicGetRequest("/does-not-exist"));
    }

}
