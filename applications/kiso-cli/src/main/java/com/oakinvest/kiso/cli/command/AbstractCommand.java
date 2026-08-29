package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.core.validation.ValidationIssue;
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
     * Print a warning message in the console.
     *
     * @param message warning message
     */
    protected void printWarning(final String message) {
        commandSpec().commandLine().getOut().println(message);
    }

    /**
     * Print a warning message in the console.
     *
     * @param issue issue
     */
    protected void printWarning(final ValidationIssue issue) {
        printWarning(issue.severity() + " - " + issue.code() + " - " + issue.message());
    }

    /**
     * Print an error message in the console.
     *
     * @param message error message to print
     */
    protected void printError(final String message) {
        commandSpec().commandLine().getErr().println(message);
    }

    /**
     * Print a validation issue in the console.
     *
     * @param issue issue to print
     */
    protected void printError(final ValidationIssue issue) {
        printError(issue.severity() + " - " + issue.code() + " - " + issue.message());
    }

}
