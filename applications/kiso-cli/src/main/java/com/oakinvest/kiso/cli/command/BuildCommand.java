package com.oakinvest.kiso.cli.command;

import picocli.CommandLine;

/**
 * Build: Generates a static website from .md files, outputting HTML files alongside the original Markdown files.
 */
@CommandLine.Command(
        name = "build",
        description = "Generates a static website from .md files, outputting HTML files alongside the original Markdown files"
)
public class BuildCommand implements Runnable {

    /**
     * Run the build command.
     */
    @Override
    public void run() {
        System.out.println("Build command is not implemented yet.");
    }

}
