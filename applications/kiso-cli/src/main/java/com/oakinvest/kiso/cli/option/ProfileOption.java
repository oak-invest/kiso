package com.oakinvest.kiso.cli.option;

import picocli.CommandLine;

/**
 * Defines the profile to use.
 */
public class ProfileOption {

    /** Publishing profile. */
    @CommandLine.Option(
            names = {"-p", "--profile"},
            paramLabel = "PROFILE",
            description = "Publishing profile: .kiso/<profile>/configuration.yaml"
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
