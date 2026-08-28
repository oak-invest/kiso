package com.oakinvest.kiso.cli.option;

import picocli.CommandLine;

import java.nio.file.Path;

/**
 * Defines the source directory option for bundle-related commands.
 */
public class SourceOption {

    /** Source directory option. */
    @CommandLine.Option(
            names = {"-s", "--source"},
            defaultValue = ".",
            paramLabel = "DIRECTORY",
            description = "Directory containing the bundle. Defaults to ${DEFAULT-VALUE}."
    )
    @SuppressWarnings("unused")
    private Path sourceDirectory;

    /**
     * Source directory to read.
     *
     * @return source directory
     */
    public Path sourceDirectory() {
        return sourceDirectory;
    }

}
