package com.mtlevine0.handler;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

public interface RequestHandler {

    HttpResponse handleRequest(HttpRequest httpRequest) throws URISyntaxException, AccessDeniedException, NoSuchFileException;

}
