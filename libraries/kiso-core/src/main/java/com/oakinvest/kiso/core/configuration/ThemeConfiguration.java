package com.oakinvest.kiso.core.configuration;

/**
 * Theme configuration.
 * You can select a theme <a href="https://daisyui.com/docs/themes/?lang=fr#list-of-themes">here</a>.
 *
 * @param name theme name
 */
public record ThemeConfiguration(
        String name
) {

    /**
     * Returns an empty theme configuration.
     *
     * @return empty theme configuration
     */
    public static ThemeConfiguration empty() {
        return new ThemeConfiguration(null);
    }

}
