package com.oakinvest.kiso.core.util.types;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

/**
 * Lifecycle status of an OKF concept.
 */
public enum LifecycleStatus {

    /** Not yet reviewed; possibly incomplete. */
    DRAFT,

    /** Ready for consumption. */
    STABLE,

    /** Kept for links and history; no longer current. */
    DEPRECATED;

    /**
     * Returns a lifecycle status from frontmatter.
     *
     * @param value frontmatter status value
     * @return lifecycle status, or stable when missing or unknown
     */
    public static LifecycleStatus from(@Nullable final String value) {
        if (StringUtils.isBlank(value)) {
            return STABLE;
        }
        if (Strings.CI.equals(value, DRAFT.name())) {
            return DRAFT;
        }
        if (Strings.CI.equals(value, DEPRECATED.name())) {
            return DEPRECATED;
        }
        return STABLE;
    }

    /**
     * Returns true if the lifecycle status is one of the known values (draft, stable, deprecated).
     *
     * @param value frontmatter status value
     * @return true if the lifecycle status is one of the known values, false otherwise
     */
    public static boolean exists(@Nullable final String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(v -> v.name().equalsIgnoreCase(value.trim()));
    }

}
