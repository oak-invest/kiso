package com.oakinvest.kiso.core.configuration;

import lombok.Builder;

import java.util.List;

/**
 * Content configuration.
 *
 * @param ignorePatterns list of patterns to ignore
 */
@Builder
@SuppressWarnings("unused")
public record ContentConfiguration(
        List<String> ignorePatterns
) {

    /**
     * Constructor.
     */
    public ContentConfiguration {
        if (ignorePatterns == null) {
            ignorePatterns = List.of();
        } else {
            // Ensures ignore patterns are never null or mutable.
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
