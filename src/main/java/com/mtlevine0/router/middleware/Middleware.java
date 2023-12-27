package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.RequestHandler;

public interface Middleware extends RequestHandler {

    default boolean terminateRequest() {
        return false;
    }

}
