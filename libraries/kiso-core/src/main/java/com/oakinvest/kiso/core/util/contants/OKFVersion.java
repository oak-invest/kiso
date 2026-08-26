package com.oakinvest.kiso.core.util.contants;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

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
    public static boolean exists(@Nullable final String version) {
        if (StringUtils.isBlank(version)) {
            return false;
        }
        String normalizedVersion = StringUtils.lowerCase(StringUtils.trim(version));
        return Arrays.stream(values()).anyMatch(v -> v.getVersion().equals(version));
    }

}
