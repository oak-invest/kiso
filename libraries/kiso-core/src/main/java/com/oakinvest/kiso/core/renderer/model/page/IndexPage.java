package com.oakinvest.kiso.core.renderer.model.page;

import com.oakinvest.kiso.core.renderer.model.PageMetadata;
import com.oakinvest.kiso.core.renderer.model.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;

/**
 * Index page.
 *
 * @param metadata    the metadata of the page
 * @param bundleTree  calculated bundle tree for navigation
 * @param htmlContent the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record IndexPage(
        PageMetadata metadata,
        BundleTree bundleTree,
        HtmlContent htmlContent
) {
}
