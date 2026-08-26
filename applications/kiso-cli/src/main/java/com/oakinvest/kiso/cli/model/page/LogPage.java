package com.oakinvest.kiso.cli.model.page;

import com.oakinvest.kiso.cli.model.util.PageContext;
import com.oakinvest.kiso.cli.model.util.PageMetadata;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.NonNull;
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

    /**
     * Returns the context of the page (and specify @NonNull).
     *
     * @return the context of the page
     */
    @Override
    public @NonNull PageContext context() {
        return context;
    }

    /**
     * Returns the metadata of the page (and specify @NonNull).
     *
     * @return the metadata of the page
     */
    @Override
    public @NonNull PageMetadata metadata() {
        return metadata;
    }

}
