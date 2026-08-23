package com.oakinvest.kiso.mcp.server.util;

import picocli.CommandLine;

/**
 * Abstract command.
 */
public abstract class AbstractCommand {

    /**
     * Get the command specification injected by Picocli.
     *
     * @return command specification
     */
    protected abstract CommandLine.Model.CommandSpec commandSpec();

    /**
     * Print a blank line in the console.
     */
    protected void blankLine() {
        commandSpec().commandLine().getOut().println();
    }

    /**
     * Print a message in the console.
     *
     * @param message message to print
     */
    protected void print(final String message) {
        commandSpec().commandLine().getOut().println(message);
    }

    /**
     * Print an error message in the console.
     *
     * @param message error message to print
     */
    protected void printError(final String message) {
        commandSpec().commandLine().getErr().println(message);
    }

}
