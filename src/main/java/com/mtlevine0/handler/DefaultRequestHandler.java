package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.resource.ResourceUtil;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

public class DefaultRequestHandler implements RequestHandler {

    private String basePath;

    public DefaultRequestHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest httpRequest) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        HttpStatus httpStatus;
        if (ResourceUtil.isDirectory(basePath + httpRequest.getPath())) {
            if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.DIRECTORY_LISTING)) {
                httpStatus = HttpStatus.OK;
            } else {
                throw new NoSuchFileException("File Does Not Exist: " + httpRequest.getPath());
            }
        } else {
            ResourceUtil.loadResource(basePath + httpRequest.getPath());
            httpStatus = HttpStatus.OK;
        }
        return HttpResponse.builder()
                .status(httpStatus)
                .body(httpStatus.getReason().getBytes())
                .build();
    }
}
