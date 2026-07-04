package com.oakinvest.kiso.core.renderer.model.page;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.renderer.model.PageMetadata;
import com.oakinvest.kiso.core.renderer.model.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Concept page.
 *
 * @param siteConfiguration the site configuration
 * @param metadata          the metadata of the page
 * @param type              the type of the page
 * @param resource          A URI that uniquely identifies the underlying asset the concept describes. Absent for concepts that describe abstract ideas rather than physical resources
 * @param tags              A YAML list of short strings for cross-cutting categorization
 * @param timestamp         ISO 8601 datetime of last meaningful change
 * @param bundleTree        calculated bundle tree for navigation
 * @param htmlContent       the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record ConceptPage(
        SiteConfiguration siteConfiguration,
        PageMetadata metadata,
        String type,
        String resource,
        List<String> tags,
        OffsetDateTime timestamp,
        BundleTree bundleTree,
        HtmlContent htmlContent
) {
}
