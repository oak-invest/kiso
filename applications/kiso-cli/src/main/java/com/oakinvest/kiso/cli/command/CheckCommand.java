package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.command.SourceOption;
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

    /** Shared source directory option. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();
    /** Command spec. */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Run the build command.
     */
    @Override
    public void run() {
        commandSpec.commandLine().getOut().println("Check command");
    }

}
