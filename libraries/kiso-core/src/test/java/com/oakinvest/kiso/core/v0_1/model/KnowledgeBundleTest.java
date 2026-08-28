package com.oakinvest.kiso.core.v0_1.model;

import com.oakinvest.kiso.core.BaseTest;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.oakinvest.kiso.core.util.contants.OKFConstants.ROOT_BUNDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - KnowledgeBundle Tests")
public class KnowledgeBundleTest extends BaseTest {

    @Test
    @DisplayName("Testing KnowledgeBundle load")
    void load() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);
        var rootBundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing .bundles() ==========================================================================================
        assertThat(rootBundle.bundles())
                .hasSize(6)
                .satisfiesExactly(
                        bundle -> assertThat(bundle.name()).isEqualTo(ROOT_BUNDLE_NAME),
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
