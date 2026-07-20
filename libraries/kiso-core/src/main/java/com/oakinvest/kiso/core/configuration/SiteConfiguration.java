package com.oakinvest.kiso.core.configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.InternationalizationConstants.DEFAULT_LANGUAGE;

/**
 * Site configuration.
 *
 * @param baseUrl     base URL of the generated site
 * @param language    language selected (en, de...)
 * @param name        Site name
 * @param title       Index pages title
 * @param description Index pages description
 */
public record SiteConfiguration(
        @Nullable String baseUrl,
        Locale language,
        @Nullable String name,
        @Nullable String title,
        @Nullable String description
) {

    /**
     * Returns an empty site configuration.
     *
     * @return empty site configuration
     */
    public static SiteConfiguration empty() {
        return new SiteConfiguration(null, DEFAULT_LANGUAGE, null, null, null);
    }

    /**
     * Returns the base URL with a trailing slash.
     *
     * @return normalized base URL, or an empty string when none is configured
     */
    public String normalizedBaseUrl() {
        if (StringUtils.isBlank(baseUrl)) {
            return "";
        }
        if (Strings.CI.endsWith(baseUrl, "/")) {
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
        return Objects.requireNonNullElse(language, DEFAULT_LANGUAGE).toLanguageTag();
    }

}
