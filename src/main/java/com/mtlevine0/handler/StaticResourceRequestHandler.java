package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.resource.DirectoryUtil;
import com.mtlevine0.resource.ResourceUtil;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class StaticResourceRequestHandler implements RequestHandler {

    private final String basePath;

    public StaticResourceRequestHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws IOException {
        byte[] body;
        if (ResourceUtil.isDirectory(basePath, request.getPath())) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.DIRECTORY_LISTING)) {
                body = DirectoryUtil.lsDir(basePath, request.getPath()).getBytes();
            } else {
                throw new NoSuchFileException("File Does Not Exist: " + request.getPath());
            }
        } else {
            body = ResourceUtil.loadResource(basePath, request.getPath());
        }
        return HttpResponse.builder().status(HttpStatus.OK).body(body).build();
    }

}
