package com.oakinvest.kiso.cli.option;

import picocli.CommandLine;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.contants.FileConstants.DEFAULT_DESTINATION_DIRECTORY_NAME;

/**
 * Defines the destination directory option for commands that create files.
 */
public class DestinationOption {

    /** Destination directory option. */
    @CommandLine.Option(
            names = {"-d", "--destination"},
            defaultValue = DEFAULT_DESTINATION_DIRECTORY_NAME,
            paramLabel = "DIRECTORY",
            description = "Directory where generated files are created. Defaults to ${DEFAULT-VALUE}."
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
