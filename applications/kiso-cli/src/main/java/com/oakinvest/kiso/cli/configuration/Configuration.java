package com.oakinvest.kiso.cli.configuration;

import com.oakinvest.kiso.core.configuration.ContentConfiguration;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import lombok.Builder;

import java.util.Objects;

/**
 * Kiso-cli configuration file.
 *
 * @param site    site configuration
 * @param theme   theme configuration
 * @param content content configuration
 */
@Builder
@SuppressWarnings("unused")
public record Configuration(
        SiteConfiguration site,
        ThemeConfiguration theme,
        ContentConfiguration content
) {

    /**
     * Creates a configuration with safe default values.
     */
    public Configuration {
        site = Objects.requireNonNullElse(site, SiteConfiguration.empty());
        theme = Objects.requireNonNullElse(theme, ThemeConfiguration.empty());
        content = Objects.requireNonNullElse(content, ContentConfiguration.empty());
    }

    /**
     * Returns an empty configuration.
     *
     * @return empty configuration
     */
    public static Configuration empty() {
        return Configuration.builder()
                .site(SiteConfiguration.empty())
                .theme(ThemeConfiguration.empty())
                .content(ContentConfiguration.empty())
                .build();
    }

}
