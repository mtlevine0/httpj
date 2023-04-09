package com.mtlevine0.router.middleware;

import com.mtlevine0.router.handlers.RequestHandler;

public interface Middleware extends RequestHandler {

    default boolean terminateRequest() {
        return false;
    }

}
