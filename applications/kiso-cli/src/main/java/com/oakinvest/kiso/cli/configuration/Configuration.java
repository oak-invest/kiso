package com.oakinvest.kiso.cli.configuration;

import com.oakinvest.kiso.core.configuration.ContentConfiguration;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;

/**
 * Kiso-cli configuration.
 *
 * @param site    site configuration
 * @param theme   theme configuration
 * @param content content configuration
 */
public record Configuration(
        SiteConfiguration site,
        ThemeConfiguration theme,
        ContentConfiguration content
) {

    /**
     * Creates a configuration with safe default values.
     */
    public Configuration {
        if (site == null) {
            site = SiteConfiguration.empty();
        }
        if (theme == null) {
            theme = ThemeConfiguration.empty();
        }
        if (content == null) {
            content = ContentConfiguration.empty();
        }
    }

    /**
     * Returns an empty configuration.
     *
     * @return empty configuration
     */
    public static Configuration empty() {
        return new Configuration(
                SiteConfiguration.empty(),
                ThemeConfiguration.empty(),
                ContentConfiguration.empty()
        );
    }

}
