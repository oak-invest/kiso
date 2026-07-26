package com.oakinvest.kiso.core.model.html.page;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Index page.
 *
 * @param siteConfiguration  the site configuration
 * @param themeConfiguration the theme configuration
 * @param metadata           the metadata of the page
 * @param bundleTree         calculated bundle tree for navigation
 * @param htmlContent        the HTML content of the page
 */
@Builder
@SuppressWarnings("unused")
public record IndexPage(
        SiteConfiguration siteConfiguration,
        ThemeConfiguration themeConfiguration,
        PageMetadata metadata,
        BundleTree bundleTree,
        @Nullable HtmlContent htmlContent
) {

    /**
     * Creates an index page with safe default values.
     */
    public IndexPage {
        siteConfiguration = Objects.requireNonNullElse(siteConfiguration, SiteConfiguration.empty());
        themeConfiguration = Objects.requireNonNullElse(themeConfiguration, ThemeConfiguration.empty());
        metadata = Objects.requireNonNullElse(metadata, PageMetadata.empty());
    }

}
