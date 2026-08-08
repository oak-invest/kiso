package com.oakinvest.kiso.core.v0_2.model;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.oakinvest.kiso.core.util.OKFConstants.ROOT_BUNDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - KnowledgeBundle Tests")
public class KnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Testing KnowledgeBundle load")
    void load() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var rootBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing .bundles() ==========================================================================================
        assertThat(rootBundle.bundles())
                .hasSize(5)
                .satisfiesExactly(
                        bundle -> assertThat(bundle.name()).isEqualTo(ROOT_BUNDLE_NAME),
                        bundle -> assertThat(bundle.name()).isEqualTo("datasets"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references/metrics"),
                        bundle -> assertThat(bundle.name()).isEqualTo("tables")
                );

        // Testing .markdownFiles() ====================================================================================
        assertThat(rootBundle.markdownFiles())
                .hasSize(14)
                .satisfiesExactly(
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("datasets/ga4_obfuscated_sample_ecommerce.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("datasets/index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/acquired_users.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/frequently_active_users.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/google_acquired_cohorts.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/highly_active_users.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/n_day_active_users.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/n_day_inactive_users.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/purchasers.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("tables/events_.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("tables/index.md")
                );
    }

    @Test
    @DisplayName("Using an empty site configuration when none is provided")
    void emptySiteConfigurationByDefault() {
        var knowledgeBundle = KnowledgeBundle.builder().build();
        assertThat(knowledgeBundle.siteConfiguration())
                .isNotNull()
                .isEqualTo(SiteConfiguration.empty());
    }

}
