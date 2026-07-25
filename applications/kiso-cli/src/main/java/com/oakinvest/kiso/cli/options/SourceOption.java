package com.oakinvest.kiso.cli.options;

import picocli.CommandLine;

import java.nio.file.Path;

/**
 * Source directory option for commands reading a bundle.
 */
public class SourceOption {

    /** Source directory option. */
    @CommandLine.Option(
            names = {"-s", "--source"},
            defaultValue = ".",
            paramLabel = "DIRECTORY",
            description = "Directory containing the bundle to read. Defaults to the current directory."
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
