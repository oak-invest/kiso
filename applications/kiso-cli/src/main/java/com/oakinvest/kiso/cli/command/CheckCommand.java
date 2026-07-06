package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.options.SourceOption;
import com.oakinvest.kiso.cli.util.AbstractCommand;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
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
public class CheckCommand extends AbstractCommand implements Callable<Integer> {

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Command specification. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Get the command specification.
     *
     * @return command specification
     */
    @Override
    protected CommandLine.Model.CommandSpec commandSpec() {
        return commandSpec;
    }

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
            if (isValid(knowledgeBundle)) {
                print("No errors found.");
                return CommandLine.ExitCode.OK;
            } else {
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

}
