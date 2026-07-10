package com.oakinvest.kiso.core.renderer.model.page;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.renderer.model.PageMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Check default configurations when not specified")
class PageDefaultValuesTest {

    @Test
    @DisplayName("Concept page uses empty configuration and metadata by default")
    void conceptPageDefaultValues() {
        var page = ConceptPage.builder().build();

        assertDefaultValues(page.siteConfiguration(), page.themeConfiguration(), page.metadata());
    }

    @Test
    @DisplayName("Index page uses empty configuration and metadata by default")
    void indexPageDefaultValues() {
        var page = IndexPage.builder().build();

        assertDefaultValues(page.siteConfiguration(), page.themeConfiguration(), page.metadata());
    }

    @Test
    @DisplayName("Log page uses empty configuration and metadata by default")
    void logPageDefaultValues() {
        var page = LogPage.builder().build();

        assertDefaultValues(page.siteConfiguration(), page.themeConfiguration(), page.metadata());
    }

    private void assertDefaultValues(final SiteConfiguration siteConfiguration,
                                     final ThemeConfiguration themeConfiguration,
                                     final PageMetadata metadata) {
        assertThat(siteConfiguration).isEqualTo(SiteConfiguration.empty());
        assertThat(themeConfiguration).isEqualTo(ThemeConfiguration.empty());
        assertThat(metadata).isEqualTo(PageMetadata.empty());
    }

}
