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

public class RequestRouterStaticServerEnabledTest extends RequestRouterTest {

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

}
