package com.oakinvest.kiso.core.model.search;

import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Search index.
 *
 * @param documents List of documents to search on
 */
@Builder
@SuppressWarnings("unused")
public record SearchIndex(
        List<SearchDocument> documents
) {

    /**
     * Creates a SearchIndex with safe default values.
     */
    public SearchIndex {
        documents = Objects.requireNonNullElse(documents, List.of());
    }

}
