package com.oakinvest.kiso.core.model.html.page;

import com.oakinvest.kiso.core.model.html.util.PageContext;
import com.oakinvest.kiso.core.model.html.util.PageMetadata;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Concept page.
 *
 * @param context     the context of the page
 * @param metadata    the metadata of the page
 * @param type        the type of the page
 * @param resource    a URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources
 * @param tags        a YAML list of short strings for cross-cutting categorization
 * @param timestamp   ISO 8601 datetime of the last meaningful change
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record ConceptPage(
        PageContext context,
        PageMetadata metadata,
        String type,
        @Nullable String resource,
        List<String> tags,
        @Nullable OffsetDateTime timestamp,
        @Nullable HtmlContent htmlContent
) {

    /**
     * Creates a concept page with safe default values.
     */
    public ConceptPage {
        context = Objects.requireNonNullElse(context, PageContext.empty());
        metadata = Objects.requireNonNullElse(metadata, PageMetadata.empty());
    }

}
