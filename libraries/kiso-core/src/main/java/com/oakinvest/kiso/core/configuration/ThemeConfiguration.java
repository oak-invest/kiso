package com.oakinvest.kiso.core.configuration;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Theme configuration.
 * You can select a theme <a href="https://daisyui.com/docs/themes/?lang=fr#list-of-themes">here</a>.
 *
 * @param name theme name
 */
@Builder
@SuppressWarnings("unused")
public record ThemeConfiguration(
        String name
) {

    /** Default theme. */
    public static final String DEFAULT_THEME = "light";

    /**
     * Returns an empty theme configuration.
     *
     * @return empty theme configuration
     */
    public static ThemeConfiguration empty() {
        return new ThemeConfiguration(DEFAULT_THEME);
    }

    /**
     * Returns a theme name (if not specified, returns the default theme).
     *
     * @return theme name
     */
    public String effectiveName() {
        if (StringUtils.isBlank(name)) {
            return DEFAULT_THEME;
        }
        return name.strip().toLowerCase(Locale.ROOT);
    }

}
