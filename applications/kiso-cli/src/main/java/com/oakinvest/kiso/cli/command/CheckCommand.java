package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.options.SourceOption;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import com.oakinvest.kiso.core.validation.ValidationRunner;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Check: Validate Markdown files and report formatting or structural errors.
 */
@CommandLine.Command(
        name = "check",
        mixinStandardHelpOptions = true,
        description = "Validate Markdown files and report formatting or structural errors"
)
public class CheckCommand implements Callable<Integer> {

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Command spec. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Run the check command.
     */
    @Override
    public Integer call() {
        // Displaying information about the process ====================================================================
        final File sourceDirectory = sourceOption.sourceDirectory().toFile();
        print("Kiso-cli - Running check command");
        print("Sources in " + sourceDirectory.getAbsolutePath());
        blankLine();

        // Running the validation ======================================================================================
        try {

            final KnowledgeBundle knowledgeBundle = KnowledgeBundleLoader.load(sourceDirectory.toPath(), SiteConfiguration.empty());
            final ValidationRunner validationRunner = new ValidationRunner();

            final var report = validationRunner.runValidation(knowledgeBundle);
            if (!report.hasErrors()) {
                print("No errors found.");
                return CommandLine.ExitCode.OK;
            } else {
                // Errors were found ===================================================================================
                report.issues().forEach(this::printError);
                return CommandLine.ExitCode.SOFTWARE;
            }

        } catch (KnowledgeBundleLoadingException e) {
            printError(e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

    /**
     * Print message in console.
     *
     * @param message message to print
     */
    private void print(final String message) {
        commandSpec.commandLine().getOut().println(message);
    }

    /**
     * Blank line in console.
     */
    private void blankLine() {
        commandSpec.commandLine().getOut().println();
    }

    /**
     * Print an issue.
     *
     * @param issue issue to print
     */
    private void printError(final ValidationIssue issue) {
        printError(issue.severity() + " - " + issue.code() + " - " + issue.message());
    }

    /**
     * Print error message in console.
     *
     * @param message message to print
     */
    private void printError(final String message) {
        commandSpec.commandLine().getErr().println(message);
    }

}
