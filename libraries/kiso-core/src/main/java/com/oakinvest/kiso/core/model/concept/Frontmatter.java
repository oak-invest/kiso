package com.oakinvest.kiso.core.model.concept;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Frontmatter: YAML metadata block delimited by --- at the top of a Markdown file
 *
 * @param type concept type
 * @param title optional display title
 * @param description optional one-line summary
 * @param resource optional canonical resource URI
 * @param tags optional categorization tags
 * @param timestamp optional last meaningful change timestamp
 * @param extraFields producer-defined fields not modeled by Kiso
 */
public record Frontmatter(
        String type,
        String title,
        String description,
        String resource,
        List<String> tags,
        OffsetDateTime timestamp,
        Map<String, Object> extraFields
) {
}
