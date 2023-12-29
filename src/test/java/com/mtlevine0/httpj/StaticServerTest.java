package com.mtlevine0.httpj;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.router.handlers.StaticResourceRequestHandler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class StaticServerTest {
    private static Logger LOGGER = Logger.getLogger(HttpServerTest.class.getName());

    public static void main( String[] args ) throws IOException {
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.DIRECTORY_LISTING);
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.STATIC_FILE_SERVER);
        LOGGER.info("Starting httpj Server...");
        logFeatureFlags();

        RequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler("/Users/matt/Dev/projects/httpj/");
        HttpJ httpJServer = new HttpJ(staticResourceRequestHandler);
        httpJServer.start(8080);
    }

    private static void logFeatureFlags() {
        Map<FeatureFlag, Boolean> flags = FeatureFlagContext.getInstance().getFeatures();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (FeatureFlag flag : flags.keySet()) {
            sb.append("Feature: " + flag.name() + " - " + flags.get(flag).toString() + "\n");
        }
        LOGGER.info(sb.toString());
    }

}

