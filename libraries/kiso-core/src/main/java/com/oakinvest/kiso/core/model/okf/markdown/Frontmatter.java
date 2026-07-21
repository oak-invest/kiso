package com.oakinvest.kiso.core.model.okf.markdown;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Frontmatter associated to a Markdown file.
 *
 * @param type        A short string identifying the kind of concept. Consumers use this for routing, filtering, and presentation. Example values: BigQuery Table, BigQuery Dataset, API Endpoint, Metric, Playbook, Reference
 * @param title       Human-readable display name. If omitted, consumers MAY derive a title from the filename.
 * @param description A single sentence summarizing the concept. Used by index.md generators, search snippets, and previews.
 * @param resource    A URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources.
 * @param tags        A YAML list of short strings for cross-cutting categorization.
 * @param timestamp   ISO 8601 datetime of last meaningful change.
 * @param extraFields producer-defined fields not modeled by OKF.
 */
@Builder
@SuppressWarnings("unused")
public record Frontmatter(
        @Nullable String type,
        @Nullable String title,
        @Nullable String description,
        @Nullable String resource,
        List<String> tags,
        @Nullable String timestamp,
        Map<String, Object> extraFields
) {

    /**
     * Empty Frontmatter.
     *
     * @return empty frontmatter
     */
    public static Frontmatter empty() {
        return new Frontmatter(
                null,
                null,
                null,
                null,
                List.of(),
                null,
                Map.of()
        );
    }

    /**
     * Returns parsed timestamp.
     *
     * @return timestamp as OffsetDateTime or null if timestamp is blank or not parsable
     */
    public @Nullable OffsetDateTime parsedTimestamp() {
        if (StringUtils.isBlank(timestamp)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(timestamp);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

}
