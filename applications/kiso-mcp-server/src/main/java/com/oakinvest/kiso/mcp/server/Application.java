package com.oakinvest.kiso.mcp.server;

import com.oakinvest.kiso.mcp.server.command.ServeCommand;
import picocli.CommandLine;

import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * Application kiso-mcp-server.
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
        // Hides expected Lucene warnings for GraalVM native images while preserving errors.
        Logger.getLogger("org.apache.lucene.util.HotspotVMOptions").setLevel(SEVERE);
        Logger.getLogger("org.apache.lucene.internal.vectorization.VectorizationProvider").setLevel(SEVERE);
        new CommandLine(new Application()).execute(args);
    }

    @Override
    public final void run() {
        serveCommand.run();
    }

}
