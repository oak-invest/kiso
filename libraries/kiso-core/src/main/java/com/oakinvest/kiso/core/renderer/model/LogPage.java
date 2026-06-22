package com.oakinvest.kiso.core.renderer.model;

import com.oakinvest.kiso.core.renderer.util.PageMetadata;
import gg.jte.html.HtmlContent;
import lombok.Builder;

/**
 * Log page.
 *
 * @param metadata    the metadata of the page
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record LogPage(
        PageMetadata metadata,
        HtmlContent htmlContent
) {
}
