package com.oakinvest.kiso.core.configuration;

import org.apache.commons.lang3.StringUtils;

/**
 * Theme configuration.
 * You can select a theme <a href="https://daisyui.com/docs/themes/?lang=fr#list-of-themes">here</a>.
 *
 * @param name theme name
 */
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
        return new ThemeConfiguration(null);
    }

    /**
     * Returns a theme name even if configuration parameter is empty.
     *
     * @return theme name
     */
    public String effectiveName() {
        if (StringUtils.isBlank(name)) {
            return DEFAULT_THEME;
        } else {
            return name;
        }
    }

}
