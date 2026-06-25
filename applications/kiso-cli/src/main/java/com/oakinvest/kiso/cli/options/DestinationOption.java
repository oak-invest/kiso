package com.oakinvest.kiso.cli.options;

import picocli.CommandLine;

import java.nio.file.Path;

/**
 * Shared destination directory option for commands creating files.
 */
public class DestinationOption {

    /**
     * Destination directory option.
     */
    @CommandLine.Option(
            names = {"-d", "--destination"},
            defaultValue = "public",
            paramLabel = "DIRECTORY",
            description = "Directory where generated files are created. Defaults to the public directory."
    )
    private Path destinationDirectory;

    /**
     * Destination directory where files are created.
     *
     * @return destination directory
     */
    public Path destinationDirectory() {
        return destinationDirectory;
    }

}
