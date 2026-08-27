package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.ApplicationVersion;
import com.oakinvest.kiso.cli.configuration.Configuration;
import com.oakinvest.kiso.cli.configuration.ConfigurationLoader;
import com.oakinvest.kiso.cli.options.ProfileOption;
import com.oakinvest.kiso.cli.options.SourceOption;
import com.oakinvest.kiso.cli.util.AbstractCommand;
import com.oakinvest.kiso.cli.util.IgnorePatternMatcher;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.ValidationReport;
import com.oakinvest.kiso.core.validation.ValidationRunner;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Check: Validate bundles and Markdown files and report formatting or structural errors.
 */
@CommandLine.Command(
        name = "check",
        mixinStandardHelpOptions = true,
        description = "Validate bundles and Markdown files and report formatting or structural errors"
)
public class CheckCommand extends AbstractCommand implements Callable<Integer> {

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Profile option. */
    @CommandLine.Mixin
    private final ProfileOption profileOption = new ProfileOption();

    /** Command specification. */
    @SuppressWarnings("unused")
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
        print("Kiso-cli " + ApplicationVersion.get() + " - Running check command");
        print("Sources in " + sourceDirectory.getAbsolutePath());
        blankLine();

        Path temporaryDirectory = null;
        try {

            // Loading configuration & profile =========================================================================
            final String profile = profileOption.profile();
            final Configuration configuration = ConfigurationLoader
                    .load(sourceDirectory.toPath(), profile)
                    .orElse(Configuration.empty());

            // Copying user okf bundle files to the destination directory ==============================================
            temporaryDirectory = Files.createTempDirectory("kiso-");
            final IgnorePatternMatcher ignorePatternMatcher = new IgnorePatternMatcher(configuration.content().ignorePatterns());
            final FileFilter fileFilter = file -> {
                Path relativePath = sourceDirectory.toPath().relativize(file.toPath());
                return !ignorePatternMatcher.matches(relativePath);
            };
            FileUtils.copyDirectory(sourceDirectory, temporaryDirectory.toFile(), fileFilter);

            // Running the validation ==================================================================================
            final KnowledgeBundle knowledgeBundle = KnowledgeBundleLoader.load(temporaryDirectory);
            final ValidationReport validationReport = ValidationRunner.runValidation(knowledgeBundle);
            // Print warnings.
            validationReport.warnings().forEach(this::printWarning);
            if (validationReport.hasErrors()) {
                // Print errors.
                validationReport.errors().forEach(this::printError);
                return CommandLine.ExitCode.SOFTWARE;
            } else {
                // No errors found.
                print("No errors found.");
                return CommandLine.ExitCode.OK;
            }

        } catch (KnowledgeBundleLoadingException e) {
            printError("Error loading knowledge bundle: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        } finally {
            if (temporaryDirectory != null) {
                try {
                    FileUtils.deleteDirectory(temporaryDirectory.toFile());
                } catch (IOException e) {
                    printWarning("Failed to delete temporary directory: " + e.getMessage());
                }
            }
        }
    }

}
