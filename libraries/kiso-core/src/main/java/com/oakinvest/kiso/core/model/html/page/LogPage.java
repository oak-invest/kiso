package com.oakinvest.kiso.core.model.html.page;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Log page.
 *
 * @param siteConfiguration  the site configuration
 * @param themeConfiguration the theme configuration
 * @param metadata           the metadata of the page
 * @param bundleTree         calculated bundle tree for navigation
 * @param htmlContent        the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record LogPage(
        SiteConfiguration siteConfiguration,
        ThemeConfiguration themeConfiguration,
        PageMetadata metadata,
        BundleTree bundleTree,
        @Nullable HtmlContent htmlContent
) {

    /**
     * Creates a log page with safe default values.
     */
    public LogPage {
        siteConfiguration = Objects.requireNonNullElse(siteConfiguration, SiteConfiguration.empty());
        themeConfiguration = Objects.requireNonNullElse(themeConfiguration, ThemeConfiguration.empty());
        metadata = Objects.requireNonNullElse(metadata, PageMetadata.empty());
    }

}
