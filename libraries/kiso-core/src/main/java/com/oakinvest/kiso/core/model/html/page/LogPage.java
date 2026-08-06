package com.oakinvest.kiso.core.model.html.page;

import com.oakinvest.kiso.core.model.html.util.PageContext;
import com.oakinvest.kiso.core.model.html.util.PageMetadata;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Log page.
 *
 * @param context     the context of the page
 * @param metadata    the metadata of the page
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record LogPage(
        PageContext context,
        PageMetadata metadata,
        @Nullable HtmlContent htmlContent
) {

    /**
     * Creates a log page with safe default values.
     */
    public LogPage {
        context = Objects.requireNonNullElse(context, PageContext.empty());
        metadata = Objects.requireNonNullElse(metadata, PageMetadata.empty());
    }

}
