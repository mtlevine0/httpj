package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpMethod;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertEquals;

public class RequestRouterStaticServerEnabledTest extends RequestHandlerTest {

    @Before
    public void setup() {
        basePath = "../httpj";
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.STATIC_FILE_SERVER);
        requestRouter = new RequestRouter(basePath);
        requestRouter.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldExecuteRequestHandler_WhenRequestHandlerRegisteredToWildCardRouteAndHttpRequestMatchesRoute() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        requestRouter.registerRoute("*", HttpMethod.GET, new RequestRouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldExecuteWildCardPathCustomRequestHandler_WhenRequestPathDoesNotMatchStaticPathRoutes() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        requestRouter.registerRoute("*", HttpMethod.GET, new RequestRouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/")));
    }

    @Test(expected = NoSuchFileException.class)
    public void ShouldThrowMethodNotAllowedException_WhenRequestDoesNotMatchRoute() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        requestRouter.route(getBasicGetRequest("/does-not-exist"));
    }

    @Test
    public void ShouldReturnResource_WhenRequestPathMatchesValidResource() throws AccessDeniedException, NoSuchFileException, URISyntaxException {
        assertEquals(getBasicHttpResponse("this is a test file"), requestRouter.route(getBasicGetRequest("/src/main/resources/images/test.txt")));
    }

}
