package com.mtlevine0.httpj;

import com.mtlevine0.router.handlers.StaticResourceRequestHandler;
import picocli.CommandLine;

import java.util.logging.Logger;

@CommandLine.Command(name = "httpj", mixinStandardHelpOptions = true)
public class StaticServer implements Runnable {
    private static Logger LOGGER = Logger.getLogger(StaticServer.class.getName());

    @CommandLine.Option(names = { "-p", "--port" }, description = "Server port")
    private int port = 8080;

    @CommandLine.Parameters(paramLabel = "<path>", description = "Base path to serve")
    private String[] path = { "./" };

    @Override
    public void run() {
        LOGGER.info(String.format("Starting httpj Server on port: %s\n" +
                "Serving directory contents: %s", port, path[0]));
        HttpJ httpJServer = new HttpJ(new StaticResourceRequestHandler(path[0]));
        httpJServer.start(port);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new StaticServer()).execute(args);
        System.exit(exitCode);
    }
}
