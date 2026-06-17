package com.oakinvest.kiso.cli;

import picocli.CommandLine;

@CommandLine.Command(
        name = "kiso-cli",
        mixinStandardHelpOptions = true,
        subcommands = {
            BuildCommand.class
        }
)
public class Application implements Runnable {

    /**
     * Main method.
     * @param args arguments
     */
    public static void main(final String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

}
