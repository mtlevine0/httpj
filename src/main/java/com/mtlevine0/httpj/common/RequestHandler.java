package com.mtlevine0.httpj.common;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

public interface RequestHandler {

    void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
