package com.oakinvest.kiso.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagSlugifier Tests")
public class TagSlugifierTest {

    @Test
    @DisplayName("Creating URL-safe slugs from tags")
    void slug() {
        assertThat(TagSlugifier.slug("basic queries")).isEqualTo("basic-queries");
        assertThat(TagSlugifier.slug("données financières")).isEqualTo("donnees-financieres");
        assertThat(TagSlugifier.slug(" Google Analytics ")).isEqualTo("google-analytics");
        assertThat(TagSlugifier.slug("e-commerce")).isEqualTo("e-commerce");
        assertThat(TagSlugifier.slug("!!!")).isEqualTo("tag");
    }

}
