package com.mtlevine0.router;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.router.utils.DirectoryUtil;
import com.mtlevine0.router.utils.ResourceUtil;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class StaticResourceRequestHandler implements RequestHandler {

    private final String basePath;

    public StaticResourceRequestHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
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
        response.setBody(body);
        response.setStatus(HttpStatus.OK);
    }

}
