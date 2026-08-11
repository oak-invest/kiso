package com.oakinvest.kiso.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

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
        if (Strings.CI.equals(value, "draft")) {
            return DRAFT;
        }
        if (Strings.CI.equals(value, "deprecated")) {
            return DEPRECATED;
        }
        return STABLE;
    }

}
