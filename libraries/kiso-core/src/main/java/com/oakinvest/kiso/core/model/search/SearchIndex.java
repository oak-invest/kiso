package com.oakinvest.kiso.core.model.search;

import lombok.Builder;

import java.util.List;

/**
 * Search index.
 *
 * @param documents List of search documents
 */
@Builder
@SuppressWarnings("unused")
public record SearchIndex(
        List<SearchDocument> documents
) {

    /**
     * Default constructor.
     */
    public SearchIndex {
        if (documents == null) {
            documents = List.of();
        }
    }

}
