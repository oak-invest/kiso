package com.oakinvest.kiso.cli.configuration;

/**
 * Kiso-cli configuration.
 *
 * @param site    site configuration
 * @param theme   theme configuration
 * @param content content configuration
 */
public record KisoCliConfiguration(
        SiteConfiguration site,
        ThemeConfiguration theme,
        ContentConfiguration content
) {

    /**
     * Creates a configuration with safe default values.
     */
    public KisoCliConfiguration {
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
    public static KisoCliConfiguration empty() {
        return new KisoCliConfiguration(null, null, null);
    }

}
