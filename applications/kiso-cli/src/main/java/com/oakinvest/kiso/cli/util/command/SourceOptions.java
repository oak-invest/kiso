package com.oakinvest.kiso.cli.util.command;

import java.nio.file.Path;
import picocli.CommandLine;

/**
 * Shared source directory option for commands reading a bundle.
 */
public class SourceOptions {

    /**
     * Source directory option.
     */
    @CommandLine.Option(
            names = {"-s", "--source"},
            defaultValue = ".",
            paramLabel = "DIRECTORY",
            description = "Directory containing the Markdown files to read. Defaults to the current directory."
    )
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
