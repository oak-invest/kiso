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
    V0_1("0.1"),

    /** Version 0.2. */
    V0_2("0.2");

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
        return Arrays.stream(values()).anyMatch(value -> value.getVersion().equals(StringUtils.trim(version)));
    }

    /**
     * Checks the major.minor format independently of known versions, ignoring surrounding whitespace.
     *
     * @param version the version to check
     * @return true if the version contains two non-negative integers separated by a dot
     */
    public static boolean isValidFormat(@Nullable final String version) {
        if (StringUtils.isBlank(version)) {
            return false;
        }
        return StringUtils.trim(version).matches("[0-9]+\\.[0-9]+");
    }

}
