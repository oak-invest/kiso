package com.oakinvest.kiso.cli.configuration;

/**
 * Kiso-cli configuration.
 *
 * @param site    site configuration
 * @param theme   theme configuration
 * @param content content configuration
 */
public record Configuration(
        com.oakinvest.kiso.core.configuration.SiteConfiguration site,
        com.oakinvest.kiso.core.configuration.ThemeConfiguration theme,
        com.oakinvest.kiso.core.configuration.ContentConfiguration content
) {

    /**
     * Creates a configuration with safe default values.
     */
    public Configuration {
        if (site == null) {
            site = com.oakinvest.kiso.core.configuration.SiteConfiguration.empty();
        }
        if (theme == null) {
            theme = com.oakinvest.kiso.core.configuration.ThemeConfiguration.empty();
        }
        if (content == null) {
            content = com.oakinvest.kiso.core.configuration.ContentConfiguration.empty();
        }
    }

    /**
     * Returns an empty configuration.
     *
     * @return empty configuration
     */
    public static Configuration empty() {
        return new Configuration(null, null, null);
    }

}
