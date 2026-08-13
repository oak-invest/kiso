package com.oakinvest.kiso.core.util.contants;

import lombok.Getter;

/**
 * OKF version constants.
 */
@SuppressWarnings("unused")
public enum OKFVersion {

    /** Version 0.1. */
    V0_1("v0.1"),

    /** Version 0.2. */
    V0_2("v0.2");

    /** The version string. */
    @Getter
    private final String version;

    /**
     * Constructor for OKFVersion.
     *
     * @param newVersion version
     */
    OKFVersion(final String newVersion) {
        this.version = newVersion;
    }

    /**
     * Checks if the given version exists in the enum.
     *
     * @param version the version to check
     * @return true if the version exists, false otherwise
     */
    public static boolean exists(final String version) {
        for (OKFVersion okfVersion : values()) {
            if (okfVersion.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }

}
