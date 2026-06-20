package com.oakinvest.kiso.core.loader;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Loading a knowledge bundle from the Google example directory.
 */
class GoogleExampleLoaderTest extends BaseTest {

    @Test
    @DisplayName("Loading google example bundle")
    void loadShouldReadDirectoriesAndMarkdownFiles() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = new KnowledgeBundleLoader().load(resourcePath);

        // Testing root bundle =========================================================================================
        assertThat(bundle.rootBundle().childBundleDirectories()).hasSize(3);
        assertThat(bundle.rootBundle().markdownFiles())
                .hasSize(1)
                // index ===============================================================================================
                // /index
                .satisfiesExactly(index -> {
                    // Type.
                    assertThat(index.kind()).isEqualTo(INDEX);

                    // Path.
                    assertThat(index.path()).isEqualTo(Path.of(resourcePath + "/index.md"));
                    assertThat(index.relativePath()).isEqualTo(Path.of("index.md"));

                    // Frontmatter.
                    assertThat(index.frontmatter())
                            .isNotNull()
                            .returns(null, Frontmatter::type)
                            .returns(null, Frontmatter::resource)
                            .returns(null, Frontmatter::title)
                            .returns(null, Frontmatter::description)
                            .returns(List.of(), Frontmatter::tags)
                            .returns(null, Frontmatter::timestamp);

                    // Content.
                    assertThat(index.content()).contains("# Subdirectories");
                });

        // "datasets" bundle ===========================================================================================
        var datasetBundle = bundle.rootBundle().childBundleDirectories().getFirst();
        assertThat(datasetBundle)
                .returns(Path.of(resourcePath + "/datasets"), Bundle::path)
                .returns(Path.of("datasets"), Bundle::relativePath)
                .returns(List.of(), Bundle::childBundleDirectories);

        assertThat(datasetBundle.markdownFiles())
                .hasSize(2)
                .satisfiesExactly(
                        // ga4 =========================================================================================
                        // /datasets/ga4_obfuscated_sample_ecommerce.md
                        ga4 -> {
                            // Type.
                            assertThat(ga4.kind()).isEqualTo(CONCEPT);

                            // Path.
                            assertThat(ga4.path()).isEqualTo(Path.of(resourcePath + "/datasets/ga4_obfuscated_sample_ecommerce.md"));
                            assertThat(ga4.relativePath()).isEqualTo(Path.of("datasets/ga4_obfuscated_sample_ecommerce.md"));

                            // Frontmatter.
                            assertThat(ga4.frontmatter())
                                    .isNotNull()
                                    .returns("BigQuery Dataset", Frontmatter::type)
                                    .returns("https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce", Frontmatter::resource)
                                    .returns("BigQuery sample dataset for Google Analytics ecommerce web implementation", Frontmatter::title)
                                    .returns("A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.", Frontmatter::description)
                                    .returns(List.of("ecommerce", "web analytics", "Google Analytics", "BigQuery", "public dataset"), Frontmatter::tags)
                                    .returns(OffsetDateTime.parse("2026-05-28T22:49:59+00:00"), Frontmatter::timestamp);

                            // Content.
                            assertThat(ga4.content()).doesNotContain("BigQuery Dataset");
                            assertThat(ga4.content()).contains("The `ga4_obfuscated_sample_ecommerce` dataset");
                        },
                        // index =======================================================================================
                        // /datasets/index
                        index -> {
                            // Type.
                            assertThat(index.kind()).isEqualTo(INDEX);

                            // Path.
                            assertThat(index.path()).isEqualTo(Path.of(resourcePath + "/datasets/index.md"));
                            assertThat(index.relativePath()).isEqualTo(Path.of("datasets/index.md"));

                            // Frontmatter.
                            assertThat(index.frontmatter())
                                    .isNotNull()
                                    .returns(null, Frontmatter::type)
                                    .returns(null, Frontmatter::resource)
                                    .returns(null, Frontmatter::title)
                                    .returns(null, Frontmatter::description)
                                    .returns(List.of(), Frontmatter::tags)
                                    .returns(null, Frontmatter::timestamp);

                            // Content.
                            assertThat(index.content()).contains("# BigQuery Dataset");
                        }
                );

        // "references" bundle =========================================================================================
        // /references
        var referencesBundle = bundle.rootBundle().childBundleDirectories().get(1);
        assertThat(referencesBundle)
                .returns(Path.of(resourcePath + "/references"), Bundle::path)
                .returns(Path.of("references"), Bundle::relativePath);

        assertThat(referencesBundle.childBundleDirectories())
                .hasSize(2)
                .satisfiesExactly(
                        // "joins" bundle ==============================================================================
                        // /references/joins
                        joins -> {
                            assertThat(joins)
                                    .returns(Path.of(resourcePath + "/references/joins"), Bundle::path)
                                    .returns(Path.of("references/joins"), Bundle::relativePath);

                            assertThat(joins.markdownFiles())
                                    .hasSize(2)
                                    .satisfiesExactly(
                                            // events file =============================================================
                                            events -> {
                                                // Type.
                                                assertThat(events.kind()).isEqualTo(CONCEPT);

                                                // Path.
                                                assertThat(events.path()).isEqualTo(Path.of(resourcePath + "/references/joins/events___ads_clickstats.md"));
                                                assertThat(events.relativePath()).isEqualTo(Path.of("references/joins/events___ads_clickstats.md"));

                                                // Frontmatter.
                                                assertThat(events.frontmatter())
                                                        .isNotNull()
                                                        .returns("Reference", Frontmatter::type)
                                                        .returns("https://developers.google.com/analytics/bigquery/basic-queries", Frontmatter::resource)
                                                        .returns("Join Google Analytics Events to Google Ads Clicks", Frontmatter::title)
                                                        .returns("Join Google Analytics event data with Google Ads click data.", Frontmatter::description)
                                                        .returns(List.of("join", "Google Ads"), Frontmatter::tags)
                                                        .returns(OffsetDateTime.parse("2026-05-28T22:51:46+00:00"), Frontmatter::timestamp);

                                                // Content.
                                                assertThat(events.content()).doesNotContain("Join Google Analytics Events to Google Ads Clicks");
                                                assertThat(events.content()).contains("Join Google Analytics event data with Google Ads click data");
                                            },
                                            index -> {
                                                // Type.
                                                assertThat(index.kind()).isEqualTo(INDEX);
                                            }
                                    );
                        },
                        // "metrics" bundle ============================================================================
                        // /references/metrics
                        metrics -> {
                            assertThat(metrics)
                                    .returns(Path.of(resourcePath + "/references/metrics"), Bundle::path)
                                    .returns(Path.of("references/metrics"), Bundle::relativePath);

                            assertThat(metrics.markdownFiles()).hasSize(9);
                        }
                );
    }

}
