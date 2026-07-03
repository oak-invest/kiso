package com.oakinvest.kiso.cli.configuration;

import java.util.Locale;

/**
 * Site configuration.
 *
 * @param language    language selected (en, fr, de...)
 * @param title       Index page title
 * @param description Index page description
 */
public record SiteConfiguration(
        Locale language,
        String title,
        String description
) {

    /**
     * Returns an empty site configuration.
     *
     * @return empty site configuration
     */
    public static SiteConfiguration empty() {
        return new SiteConfiguration(null, null, null);
    }

}
