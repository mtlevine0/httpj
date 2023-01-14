package com.mtlevine0.router;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.httpj.request.HttpMethod;

import com.mtlevine0.httpj.response.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RouterStaticServerEnabledTest extends RouterTest {

    @Before
    public void setup() {
        basePath = "../httpj";
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.STATIC_FILE_SERVER);
        router = new Router(basePath);
        router.registerRoute("/test", HttpMethod.GET, new CustomHandler());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldExecuteRequestHandler_WhenRequestHandlerRegisteredToWildCardRouteAndHttpRequestMatchesRoute() throws IOException {
        router.registerRoute("*", HttpMethod.GET, new RouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), router.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldExecuteWildCardPathCustomRequestHandler_WhenRequestPathDoesNotMatchStaticPathRoutes() throws IOException {
        router.registerRoute("*", HttpMethod.GET, new RouterStaticServerEnabledTest.DefaultRequestHandler());
        assertEquals(getBasicHttpResponse("Default Body!"), router.route(getBasicGetRequest("/"),
                HttpResponse.builder().build()));
    }

}
