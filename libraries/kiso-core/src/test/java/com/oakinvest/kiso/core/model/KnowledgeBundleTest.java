package com.oakinvest.kiso.core.model;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Using an empty site configuration when none is provided")
    void emptySiteConfigurationByDefault() {
        KnowledgeBundle knowledgeBundle = KnowledgeBundle.builder().build();

        assertThat(knowledgeBundle.siteConfiguration())
                .isNotNull()
                .isEqualTo(SiteConfiguration.empty());
    }

    @Test
    @DisplayName("Testing KnowledgeBundle.bundles()")
    void bundles() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var rootBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing .bundles() ==========================================================================================
        assertThat(rootBundle.bundles())
                .hasSize(6)
                .satisfiesExactly(
                        bundle -> assertThat(bundle.name()).isEqualTo("Index"),
                        bundle -> assertThat(bundle.name()).isEqualTo("datasets"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references/joins"),
                        bundle -> assertThat(bundle.name()).isEqualTo("references/metrics"),
                        bundle -> assertThat(bundle.name()).isEqualTo("tables")
                );

        // Testing .markdownFiles() ====================================================================================
        assertThat(rootBundle.markdownFiles())
                .hasSize(17)
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
                                .isEqualTo("references/joins/events___ads_clickstats.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/joins/index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/avg_pageviews.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/avg_spend_per_purchase_session_by_user.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/avg_transactions_per_purchaser.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/day_count.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/event_count.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/index.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/new_user_count.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/overall_avg_spend_per_purchase_session.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("references/metrics/user_count.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("tables/events_.md"),
                        markdownFile -> assertThat(markdownFile.relativePath().toString())
                                .isEqualTo("tables/index.md")
                );
    }

}
