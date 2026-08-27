package com.oakinvest.kiso.cli;

import picocli.CommandLine;

/**
 * Application version provider from PicoCLI.
 * This class is used to provide the application version to the command line interface.
 */
public class ApplicationVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public final String[] getVersion() {
        return new String[]{
                "Kiso " + ApplicationVersion.get()
        };
    }

}
