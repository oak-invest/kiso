package com.oakinvest.kiso.mcp.server;

import com.oakinvest.kiso.mcp.server.command.ServeCommand;
import picocli.CommandLine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application.
 */
@CommandLine.Command(
        name = "kiso-mcp-server",
        mixinStandardHelpOptions = true,
        versionProvider = ApplicationVersionProvider.class
)
public class Application implements Runnable {

    /** Lucene logger used when inspecting the running virtual machine. */
    private static final String LUCENE_HOTSPOT_LOGGER = "org.apache.lucene.util.HotspotVMOptions";

    /** Lucene logger used when selecting vector implementations. */
    private static final String LUCENE_VECTORIZATION_LOGGER =
            "org.apache.lucene.internal.vectorization.VectorizationProvider";

    /** Serve command. */
    @CommandLine.Mixin
    private final ServeCommand serveCommand = new ServeCommand();

    /**
     * Main method.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        configureLibraryLogging();
        new CommandLine(new Application()).execute(args);
    }

    /**
     * Hides expected Lucene warnings for GraalVM native images while preserving errors.
     */
    static void configureLibraryLogging() {
        Logger.getLogger(LUCENE_HOTSPOT_LOGGER).setLevel(Level.SEVERE);
        Logger.getLogger(LUCENE_VECTORIZATION_LOGGER).setLevel(Level.SEVERE);
    }

    @Override
    public final void run() {
        serveCommand.run();
    }

}
