package com.oakinvest.kiso.cli.util;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import com.oakinvest.kiso.core.validation.ValidationReport;
import com.oakinvest.kiso.core.validation.ValidationRunner;
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
     * Validate a knowledge bundle and print its issues when it contains errors.
     *
     * @param knowledgeBundle knowledge bundle to validate
     * @return {@code true} when the bundle contains no validation errors
     */
    protected boolean isValid(final KnowledgeBundle knowledgeBundle) {
        final ValidationReport validationReport = ValidationRunner.runValidation(knowledgeBundle);
        if (validationReport.hasErrors()) {
            validationReport.issues().forEach(this::printError);
            return false;
        }
        return true;
    }

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
     * Print warning.
     *
     * @param message warning message
     */
    protected void printWarning(final String message) {
        print("WARNING: " + message);
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
