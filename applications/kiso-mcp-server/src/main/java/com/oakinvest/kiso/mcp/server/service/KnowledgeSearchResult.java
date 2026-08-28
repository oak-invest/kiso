package com.oakinvest.kiso.mcp.server.service;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * Knowledge search result.
 *
 * @param conceptId   concept identifier
 * @param title       title of the concept
 * @param description description of the concept
 * @param score       search result score of the concept
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeSearchResult(
        String conceptId,
        @Nullable String title,
        @Nullable String description,
        float score
) {
}
