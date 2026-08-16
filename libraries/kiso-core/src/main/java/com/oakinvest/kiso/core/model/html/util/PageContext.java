package com.oakinvest.kiso.core.model.html.util;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import lombok.Builder;

import java.util.Objects;

/**
 * Page context.
 *
 * @param siteConfiguration  the site configuration
 * @param themeConfiguration the theme configuration
 * @param bundleTree         calculated bundle tree for navigation
 */
@Builder
@SuppressWarnings("unused")
public record PageContext(
        SiteConfiguration siteConfiguration,
        ThemeConfiguration themeConfiguration,
        BundleTree bundleTree
) {

    /**
     * Creates a page context with safe default values.
     */
    public PageContext {
        siteConfiguration = Objects.requireNonNullElse(siteConfiguration, SiteConfiguration.empty());
        themeConfiguration = Objects.requireNonNullElse(themeConfiguration, ThemeConfiguration.empty());
        bundleTree = Objects.requireNonNullElse(bundleTree, BundleTree.empty());
    }

    /**
     * Empty page concept.
     *
     * @return page concept
     */
    public static PageContext empty() {
        return PageContext.builder()
                .siteConfiguration(SiteConfiguration.empty())
                .themeConfiguration(ThemeConfiguration.empty())
                .bundleTree(BundleTree.empty())
                .build();
    }

}
