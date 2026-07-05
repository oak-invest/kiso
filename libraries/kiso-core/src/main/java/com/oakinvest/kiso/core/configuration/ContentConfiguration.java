package com.oakinvest.kiso.core.configuration;

import java.util.List;

/**
 * Content configuration.
 * <p>
 * Example:
 * content:
 * ignore:
 * - ".git/**"
 * - "drafts/**"
 *
 * @param ignorePatterns list of patterns to ignore
 */
public record ContentConfiguration(
        List<String> ignorePatterns
) {

    /**
     * Ensures ignore patterns are never null or mutable.
     */
    public ContentConfiguration {
        if (ignorePatterns == null) {
            ignorePatterns = List.of();
        } else {
            ignorePatterns = List.copyOf(ignorePatterns);
        }
    }

    /**
     * Returns an empty content configuration.
     *
     * @return empty content configuration
     */
    public static ContentConfiguration empty() {
        return new ContentConfiguration(List.of());
    }

}
