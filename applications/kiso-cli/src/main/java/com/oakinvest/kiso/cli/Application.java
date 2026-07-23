package com.oakinvest.kiso.cli;

import com.oakinvest.kiso.cli.command.BuildCommand;
import com.oakinvest.kiso.cli.command.CheckCommand;
import picocli.CommandLine;

/**
 * Application.
 */
@CommandLine.Command(
        name = "kiso-cli",
        mixinStandardHelpOptions = true,
        version = "v0.1.5",
        subcommands = {
                CheckCommand.class,
                BuildCommand.class
        }
)
public class Application implements Runnable {

    /**
     * Main method.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public final void run() {
        new CommandLine(this).usage(System.out);
    }

}
