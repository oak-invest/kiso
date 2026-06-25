package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Frontmatter associated to a Markdown file.
 *
 * @param type        A short string identifying the kind of concept. Consumers use this for routing, filtering, and presentation. Example values: BigQuery Table, BigQuery Dataset, API Endpoint, Metric, Playbook, Reference
 * @param title       Human-readable display fileName. If omitted, consumers MAY derive a title from the filename.
 * @param description A single sentence summarizing the concept. Used by index.md generators, search snippets, and previews.
 * @param resource    A URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources.
 * @param tags        A YAML list of short strings for cross-cutting categorization.
 * @param timestamp   ISO 8601 datetime of last meaningful change.
 * @param extraFields producer-defined fields not modeled by Kiso.
 */
@Builder
@SuppressWarnings("unused")
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
