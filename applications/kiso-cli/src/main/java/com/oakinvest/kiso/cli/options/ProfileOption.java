package com.oakinvest.kiso.cli.options;

import picocli.CommandLine;

/**
 * Publishing profile option.
 */
public class ProfileOption {

    /** Publishing profile. */
    @CommandLine.Option(
            names = {"-p", "--profile"},
            paramLabel = "PROFILE",
            description = "Publishing profile from .kiso/<profile>/configuration.yaml"
    )
    @SuppressWarnings("unused")
    private String profile;

    /**
     * Publishing profile.
     *
     * @return publishing profile
     */
    public final String profile() {
        return profile;
    }

}
