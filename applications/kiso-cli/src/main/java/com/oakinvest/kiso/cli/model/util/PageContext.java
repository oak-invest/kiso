package com.oakinvest.kiso.cli.model.util;

import com.oakinvest.kiso.cli.model.navigation.BundleTree;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import lombok.Builder;

import java.util.Objects;

/**
 * Page context.
 *
 * @param siteConfiguration  site configuration
 * @param themeConfiguration theme configuration
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
