package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpMethod;

import com.mtlevine0.response.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    public void ShouldExecuteRequestHandler_WhenRequestHandlerRegisteredToWildCardRouteAndHttpRequestMatchesRoute() throws IOException {
        requestRouter.registerRoute("*", HttpMethod.GET, new RequestRouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldExecuteWildCardPathCustomRequestHandler_WhenRequestPathDoesNotMatchStaticPathRoutes() throws IOException {
        requestRouter.registerRoute("*", HttpMethod.GET, new RequestRouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), requestRouter.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

}
