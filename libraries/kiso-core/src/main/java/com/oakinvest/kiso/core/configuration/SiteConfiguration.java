package com.oakinvest.kiso.core.configuration;

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

    /** Default language. */
    public static final Locale DEFAULT_LANGUAGE = Locale.ENGLISH;

    /**
     * Returns an empty site configuration.
     *
     * @return empty site configuration
     */
    public static SiteConfiguration empty() {
        return new SiteConfiguration(null, null, null);
    }

    /**
     * Returns the language tag.
     *
     * @return language tag
     */
    public String languageTag() {
        if (language == null) {
            return DEFAULT_LANGUAGE.toLanguageTag();
        } else {
            return language.toLanguageTag();
        }
    }

}
