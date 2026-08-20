package com.oakinvest.kiso.mcp.server;

import com.oakinvest.kiso.mcp.server.command.ServeCommand;
import picocli.CommandLine;

/**
 * Application.
 */
@CommandLine.Command(
        name = "kiso-mcp-server",
        mixinStandardHelpOptions = true,
        versionProvider = ApplicationVersionProvider.class
)
public class Application implements Runnable {

    /** Serve command. */
    @CommandLine.Mixin
    private final ServeCommand serveCommand = new ServeCommand();

    /**
     * Main method.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        new CommandLine(new Application()).execute(args);
    }

    @Override
    public final void run() {
        serveCommand.run();
    }

}
