package com.oakinvest.kiso.mcp.server.service;

import lombok.Builder;

/**
 * Knowledge search result.
 *
 * @param conceptId   concept identifier
 * @param title       title of the concept
 * @param description description of the concept
 * @param score       search score of the concept
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeSearchResult(
        String conceptId,
        String title,
        String description,
        float score
) {
}
