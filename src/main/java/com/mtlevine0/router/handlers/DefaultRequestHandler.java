package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.router.utils.ResourceUtil;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class DefaultRequestHandler implements RequestHandler {

    private String basePath;

    public DefaultRequestHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpStatus httpStatus;
        if (ResourceUtil.isDirectory(basePath, httpRequest.getPath())) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.DIRECTORY_LISTING)) {
                httpStatus = HttpStatus.OK;
            } else {
                throw new NoSuchFileException("File Does Not Exist: " + httpRequest.getPath());
            }
        } else {
            ResourceUtil.loadResource(basePath, httpRequest.getPath());
            httpStatus = HttpStatus.OK;
        }
        httpResponse.setBody(httpStatus.getReason().getBytes());
        httpResponse.setStatus(httpStatus);
    }
}
