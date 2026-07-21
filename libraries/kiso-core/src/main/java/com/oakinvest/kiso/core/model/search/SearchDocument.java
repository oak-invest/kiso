package com.oakinvest.kiso.core.model.search;

import lombok.Builder;

import java.util.List;

/**
 * Search document.
 *
 * @param id          document id
 * @param url         url of the document
 * @param title       content title
 * @param description content description
 * @param type        content type
 * @param tags        content tags
 * @param body        content body
 */
@Builder
@SuppressWarnings("unused")
public record SearchDocument(
        String id,
        String url,
        String title,
        String description,
        String type,
        List<String> tags,
        String body
) {

    /**
     * Default constructor.
     */
    public SearchDocument {
        if (tags == null) {
            tags = List.of();
        }
    }

}
