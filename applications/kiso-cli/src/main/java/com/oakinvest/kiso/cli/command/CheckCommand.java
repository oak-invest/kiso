package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.command.SourceOptions;
import picocli.CommandLine;

/**
 * Check: Validate Markdown files and report formatting or structural errors.
 */
@CommandLine.Command(
        name = "check",
        mixinStandardHelpOptions = true,
        description = "Validate Markdown files and report formatting or structural errors"
)
public class CheckCommand implements Runnable {

    /** Command spec. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOptions sourceOptions = new SourceOptions();

    /**
     * Run the build command.
     */
    @Override
    public void run() {
        commandSpec.commandLine().getOut().println("Check command");
    }

}
