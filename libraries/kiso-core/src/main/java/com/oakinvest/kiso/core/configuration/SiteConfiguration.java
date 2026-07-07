package com.oakinvest.kiso.core.configuration;

import java.util.Locale;

/**
 * Site configuration.
 *
 * @param baseUrl     public URL of the generated site
 * @param language    language selected (en, fr, de...)
 * @param title       Index page title
 * @param description Index page description
 */
public record SiteConfiguration(
        String baseUrl,
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
        return new SiteConfiguration(null, null, null, null);
    }

    /**
     * Returns the base URL with a trailing slash.
     *
     * @return normalized base URL, or an empty string when none is configured
     */
    public String normalizedBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }
        if (baseUrl.endsWith("/")) {
            return baseUrl;
        }
        return baseUrl + "/";
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
