package com.mtlevine0.handler;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.io.IOException;

public interface RequestHandler {

    HttpResponse handleRequest(HttpRequest httpRequest) throws IOException;

}
