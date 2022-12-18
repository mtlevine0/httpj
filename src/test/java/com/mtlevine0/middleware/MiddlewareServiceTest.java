package com.mtlevine0.middleware;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.handler.RequestRouter;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class MiddlewareServiceTest {
    private static final String BASE_PATH = ".";
    private MiddlewareService middlewareService;
    private RequestRouter requestRouter;

    @Before
    public void init() {
        requestRouter = new RequestRouter(BASE_PATH);
        middlewareService = new MiddlewareService(requestRouter);
    }

    @Test
    public void givenMiddlewareService_whenRegisterPreRouter_thenMiddlewareInsertedBeforeRouter() {
        Middleware loggingMiddleware = new LoggingMiddleware();
        middlewareService.registerPreRouter(loggingMiddleware);
        List<Middleware> middlewares = middlewareService.getMiddlewares();

        int routerMiddlewareIndex = 0;
        int loggingMiddlewareIndex = 0;
        int index = 0;
        for (Middleware middleware: middlewares) {
            index++;
            if (middleware instanceof RouterMiddleware) {
                routerMiddlewareIndex = index;
            }
            if (middleware == loggingMiddleware) {
                loggingMiddlewareIndex = index;
            }
        }
        assertTrue(loggingMiddlewareIndex < routerMiddlewareIndex);
    }

    @Test
    public void givenMiddlewareService_whenRegisterPostRouter_thenMiddlewareInsertedAfterRouter() {
        Middleware loggingMiddleware = new LoggingMiddleware();
        middlewareService.registerPostRouter(loggingMiddleware);
        List<Middleware> middlewares = middlewareService.getMiddlewares();

        int routerMiddlewareIndex = 0;
        int loggingMiddlewareIndex = 0;
        int index = 0;
        for (Middleware middleware: middlewares) {
            index++;
            if (middleware instanceof RouterMiddleware) {
                routerMiddlewareIndex = index;
            }
            if (middleware == loggingMiddleware) {
                loggingMiddlewareIndex = index;
            }
        }
        assertTrue(loggingMiddlewareIndex > routerMiddlewareIndex);
    }

    @Test
    public void givenMiddlewareService_whenGetMiddlewares_thenRouterMiddlewareIsRegistered() {
        List<Middleware> middlewares = middlewareService.getMiddlewares();
        assertTrue(middlewares.size() > 0);
        assertTrue(middlewares.get(0) instanceof RouterMiddleware);
    }

    @Test
    public void givenMiddlewareServiceGzipEncodingDisabled_whenGetMiddlewares_thenRouterMiddlewareIsRegistered() {
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.GZIP_ENCODING);
        middlewareService = new MiddlewareService(requestRouter);
        List<Middleware> middlewares = middlewareService.getMiddlewares();
        assertEquals(middlewares.size(), 1);
    }

    @Test
    public void givenMiddlewareWithMiddlewareServiceHandlerRegistered_whenExecuteMiddlewares_thenListMiddlewares() throws Exception{
        requestRouter.registerRoute("/middlewares", HttpMethod.GET,
                new MiddlewareService.MiddlewareServiceHandler());
        HttpResponse httpResponse = HttpResponse.builder().build();
        middlewareService.execute(generateHttpRequest(), httpResponse);
        String actualBody = new String(httpResponse.getBody());
        assertTrue(actualBody.contains(RouterMiddleware.class.getName()));
    }

    @Test(expected = MethodNotAllowedException.class)
    public void test() throws Exception{
        HttpResponse httpResponse = HttpResponse.builder().build();
        middlewareService.execute(generateHttpRequest(), httpResponse);
    }

    private HttpRequest generateHttpRequest() {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(HttpMethod.GET);
        httpRequest.setPath("/middlewares");
        return httpRequest;
    }
}
