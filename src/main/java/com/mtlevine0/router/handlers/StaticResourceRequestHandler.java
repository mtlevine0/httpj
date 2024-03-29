package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.router.utils.DirectoryUtil;
import com.mtlevine0.router.utils.ResourceUtil;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;
import lombok.SneakyThrows;

public class StaticResourceRequestHandler implements RequestHandler {

    private final String basePath;

    public StaticResourceRequestHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    @SneakyThrows
    public void handleRequest(HttpRequest request, HttpResponse response) {
        byte[] body;
        if (ResourceUtil.isDirectory(basePath, request.getPath())) {
            body = DirectoryUtil.lsDir(basePath, request.getPath()).getBytes();
        } else {
            body = ResourceUtil.loadResource(basePath, request.getPath());
        }
        response.setBody(body);
        response.setStatus(HttpStatus.OK);
    }

}
