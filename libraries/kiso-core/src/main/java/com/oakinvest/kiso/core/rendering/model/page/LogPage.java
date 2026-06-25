package com.oakinvest.kiso.core.rendering.model.page;

import com.oakinvest.kiso.core.rendering.model.PageMetadata;
import com.oakinvest.kiso.core.rendering.model.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;

/**
 * Log page.
 *
 * @param metadata    the metadata of the page
 * @param bundleTree  calculated bundle tree for navigation
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record LogPage(
        PageMetadata metadata,
        BundleTree bundleTree,
        HtmlContent htmlContent
) {
}
