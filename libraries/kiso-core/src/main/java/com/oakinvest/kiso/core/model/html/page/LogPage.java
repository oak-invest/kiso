package com.oakinvest.kiso.core.model.html.page;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.model.html.PageMetadata;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import gg.jte.html.HtmlContent;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

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
        if (siteConfiguration == null) {
            siteConfiguration = SiteConfiguration.empty();
        }
        if (themeConfiguration == null) {
            themeConfiguration = ThemeConfiguration.empty();
        }
        if (metadata == null) {
            metadata = PageMetadata.empty();
        }
    }

}
