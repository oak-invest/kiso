package com.oakinvest.kiso.core.tool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagNormalizer Tests")
public class TagNormalizerTest {

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    @DisplayName("Creating URL-safe normalizes from tags")
    void normalize() {
        assertThat(TagNormalizer.normalize("basic queries")).isEqualTo("basic-queries");
        assertThat(TagNormalizer.normalize("données financières")).isEqualTo("donnees-financieres");
        assertThat(TagNormalizer.normalize(" Google Analytics ")).isEqualTo("google-analytics");
        assertThat(TagNormalizer.normalize("e-commerce")).isEqualTo("e-commerce");
        assertThat(TagNormalizer.normalize("!!!")).isEqualTo("tag");
    }

}
