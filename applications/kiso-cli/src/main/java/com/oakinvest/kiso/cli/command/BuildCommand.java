package com.oakinvest.kiso.cli.command;

import picocli.CommandLine;

/**
 * Build command - Generates a static website with HTML files created from the .md file (and the .md files along).
 */
@CommandLine.Command(
        name = "build",
        description = "Generate a static website from OKF Markdown files."
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
