package com.oakinvest.kiso.mcp.server.option;

import lombok.Getter;
import lombok.experimental.Accessors;
import picocli.CommandLine;

import java.nio.file.Path;

/**
 * Defines the source directory option for commands serving a bundle.
 */
public class SourceOption {

    /** Source directory option. */
    @Getter
    @Accessors(fluent = true)
    @CommandLine.Option(
            names = {"-s", "--source"},
            defaultValue = ".",
            paramLabel = "DIRECTORY",
            description = "Directory containing the bundle to serve. Defaults to the current directory."
    )
    private Path sourceDirectory;

}
