package com.oakinvest.kiso.core.renderer.model;

import com.oakinvest.kiso.core.renderer.util.PageMetadata;
import gg.jte.html.HtmlContent;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Concept page.
 *
 * @param metadata    the metadata of the page
 * @param type        the type of the page
 * @param resource    A URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources.
 * @param tags        A YAML list of short strings for cross-cutting categorization.
 * @param timestamp     ISO 8601 datetime of last meaningful change.
 * @param assetBasePath relative path from this page to the generated site root
 * @param htmlContent   the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record ConceptPage(
        PageMetadata metadata,
        String type,
        String resource,
        List<String> tags,
        OffsetDateTime timestamp,
        String assetBasePath,
        HtmlContent htmlContent
) {
}
