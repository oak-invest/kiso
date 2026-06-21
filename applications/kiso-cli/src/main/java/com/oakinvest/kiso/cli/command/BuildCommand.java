package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.command.DestinationOption;
import com.oakinvest.kiso.cli.util.command.SourceOption;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

/**
 * Build: Generates a static website from .md files, outputting HTML files alongside the original Markdown files.
 */
@CommandLine.Command(
        name = "build",
        mixinStandardHelpOptions = true,
        description = "Generates a static website from .md files"
)
public class BuildCommand implements Runnable {

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Shared destination directory option. */
    @CommandLine.Mixin
    private final DestinationOption destinationOption = new DestinationOption();

    /** Command spec. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Run the build command.
     */
    @Override
    public void run() {
        try {
            // Displaying information about the process ================================================================
            final File sourceDirectory = sourceOption.sourceDirectory().toFile();
            final File destinationDirectory = destinationOption.destinationDirectory().toFile();
            commandSpec.commandLine().getOut().println("Kiso-cli");
            commandSpec.commandLine().getOut().println("Build command");
            commandSpec.commandLine().getOut().println("Using sources in " + sourceDirectory.getAbsolutePath());
            commandSpec.commandLine().getOut().println("Building website in " + destinationDirectory.getAbsolutePath());

            // Copying files ===========================================================================================
            FileUtils.deleteDirectory(destinationDirectory);
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

            // Generating HTML =========================================================================================
            final KnowledgeBundle knowledgeBundle = new KnowledgeBundleLoader().load(destinationDirectory.toPath());



        } catch (IOException e) {
            commandSpec.commandLine().getErr().println("Error: " + e.getMessage());
            commandSpec.exitCodeOnInvalidInput();
        }

    }

}
