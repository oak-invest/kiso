package com.oakinvest.kiso.cli.options;

import picocli.CommandLine;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.FileConstants.DEFAULT_DESTINATION_DIRECTORY_NAME;

/**
 * Destination directory option for commands creating files.
 */
public class DestinationOption {

    /** Destination directory option. */
    @CommandLine.Option(
            names = {"-d", "--destination"},
            defaultValue = DEFAULT_DESTINATION_DIRECTORY_NAME,
            paramLabel = "DIRECTORY",
            description = "Directory where generated files are created. Defaults to '" + DEFAULT_DESTINATION_DIRECTORY_NAME + "' directory."
    )
    @SuppressWarnings("unused")
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
