package com.mtlevine0.router;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.io.IOException;

public interface RequestHandler {

    public void handleRequest(HttpRequest httpRequest, HttpResponse response) throws IOException;

}
