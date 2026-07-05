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

class GoogleExampleLoadingTest extends BaseTest {

    @Test
    @DisplayName("Loading google example bundle")
    void googleExamplesLoading() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_EXAMPLE_DIRECTORY);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing isRoot bundle =========================================================================================
        assertThat(bundle.rootBundle().childBundles()).hasSize(3);
        assertThat(bundle.rootBundle().markdownFiles())
                .hasSize(1)
                // index ===============================================================================================
                .satisfiesExactly(index -> {
                    // File information.
                    assertThat(index.fileName()).isEqualTo("index.md");
                    assertThat(index.kind()).isEqualTo(INDEX);

                    // Path.
                    assertThat(index.absolutePath()).isEqualTo(Path.of(resourcePath + "/index.md"));
                    assertThat(index.relativePath()).isEqualTo(Path.of("index.md"));

                    // HTML.
                    assertThat(index.htmlFileName()).isEqualTo("index.html");
                    assertThat(index.htmlFilePath()).isEqualTo("index.html");

                    // Frontmatter.
                    assertThat(index.hasFrontmatter()).isFalse();

                    // Content.
                    assertThat(index.body()).contains("# Subdirectories");
                });

        // "datasets" bundle ===========================================================================================
        var datasetBundle = bundle.rootBundle().childBundles().getFirst();
        assertThat(datasetBundle)
                .returns("datasets", Bundle::name)
                .returns(Path.of(resourcePath + "/datasets"), Bundle::absolutePath)
                .returns(Path.of("datasets"), Bundle::relativePath)
                .returns(List.of(), Bundle::childBundles);

        assertThat(datasetBundle.markdownFiles())
                .hasSize(2)
                .satisfiesExactly(
                        // ga4 =========================================================================================
                        // /datasets/ga4_obfuscated_sample_ecommerce.md
                        ga4 -> {
                            // File information.
                            assertThat(ga4.fileName()).isEqualTo("ga4_obfuscated_sample_ecommerce.md");
                            assertThat(ga4.kind()).isEqualTo(CONCEPT);
                            assertThat(ga4.conceptId()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce");

                            // Path.
                            assertThat(ga4.absolutePath()).isEqualTo(Path.of(resourcePath + "/datasets/ga4_obfuscated_sample_ecommerce.md"));
                            assertThat(ga4.relativePath()).isEqualTo(Path.of("datasets/ga4_obfuscated_sample_ecommerce.md"));

                            // HTML.
                            assertThat(ga4.htmlFileName()).isEqualTo("ga4_obfuscated_sample_ecommerce.html");
                            assertThat(ga4.htmlFilePath()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce.html");

                            // Frontmatter.
                            assertThat(ga4.frontmatter())
                                    .isNotNull()
                                    .returns("BigQuery Dataset", Frontmatter::type)
                                    .returns("https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce", Frontmatter::resource)
                                    .returns("BigQuery sample dataset for Google Analytics ecommerce web implementation", Frontmatter::title)
                                    .returns("A sample of obfuscated Google Analytics BigQuery event export data for three months from the Google Merchandise Store is available as a public dataset in BigQuery.", Frontmatter::description)
                                    .returns(List.of("ecommerce", "web analytics", "Google Analytics", "BigQuery", "public dataset"), Frontmatter::tags)
                                    .returns(OffsetDateTime.parse("2026-05-28T22:49:59+00:00"), Frontmatter::parsedTimestamp);

                            // Content.
                            assertThat(ga4.body()).doesNotContain("BigQuery Dataset");
                            assertThat(ga4.body()).contains("The `ga4_obfuscated_sample_ecommerce` dataset");
                        },
                        // index =======================================================================================
                        // /datasets/index
                        index -> {
                            // File information.
                            assertThat(index.fileName()).isEqualTo("index.md");
                            assertThat(index.kind()).isEqualTo(INDEX);

                            // Path.
                            assertThat(index.absolutePath()).isEqualTo(Path.of(resourcePath + "/datasets/index.md"));
                            assertThat(index.relativePath()).isEqualTo(Path.of("datasets/index.md"));

                            // HTML.
                            assertThat(index.htmlFileName()).isEqualTo("index.html");
                            assertThat(index.htmlFilePath()).isEqualTo("datasets/index.html");

                            // Frontmatter.
                            assertThat(index.hasFrontmatter()).isFalse();

                            // Content.
                            assertThat(index.body()).contains("# BigQuery Dataset");
                        }
                );

        // "references" bundle =========================================================================================
        // /references
        var referencesBundle = bundle.rootBundle().childBundles().get(1);
        assertThat(referencesBundle)
                .returns(Path.of(resourcePath + "/references"), Bundle::absolutePath)
                .returns(Path.of("references"), Bundle::relativePath);

        assertThat(referencesBundle.childBundles())
                .hasSize(2)
                .satisfiesExactly(
                        // "joins" bundle ==============================================================================
                        // /references/joins
                        joins -> {
                            assertThat(joins)
                                    .returns("references/joins", Bundle::name)
                                    .returns(Path.of(resourcePath + "/references/joins"), Bundle::absolutePath)
                                    .returns(Path.of("references/joins"), Bundle::relativePath);

                            assertThat(joins.markdownFiles())
                                    .hasSize(2)
                                    .satisfiesExactly(
                                            // events file =============================================================
                                            events -> {
                                                // File information.
                                                assertThat(events.fileName()).isEqualTo("events___ads_clickstats.md");
                                                assertThat(events.kind()).isEqualTo(CONCEPT);

                                                // Filename.
                                                assertThat(events.fileName()).isEqualTo("events___ads_clickstats.md");
                                                assertThat(events.htmlFileName()).isEqualTo("events___ads_clickstats.html");

                                                // Path.
                                                assertThat(events.absolutePath()).isEqualTo(Path.of(resourcePath + "/references/joins/events___ads_clickstats.md"));
                                                assertThat(events.relativePath()).isEqualTo(Path.of("references/joins/events___ads_clickstats.md"));

                                                // HTML.
                                                assertThat(events.htmlFileName()).isEqualTo("events___ads_clickstats.html");
                                                assertThat(events.htmlFilePath()).isEqualTo("references/joins/events___ads_clickstats.html");

                                                // Frontmatter.
                                                assertThat(events.frontmatter())
                                                        .isNotNull()
                                                        .returns("Reference", Frontmatter::type)
                                                        .returns("https://developers.google.com/analytics/bigquery/basic-queries", Frontmatter::resource)
                                                        .returns("Join Google Analytics Events to Google Ads Clicks", Frontmatter::title)
                                                        .returns("Join Google Analytics event data with Google Ads click data.", Frontmatter::description)
                                                        .returns(List.of("join", "Google Ads"), Frontmatter::tags)
                                                        .returns(OffsetDateTime.parse("2026-05-28T22:51:46+00:00"), Frontmatter::parsedTimestamp);

                                                // Content.
                                                assertThat(events.body()).doesNotContain("Join Google Analytics Events to Google Ads Clicks");
                                                assertThat(events.body()).contains("Join Google Analytics event data with Google Ads click data");
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
                                    .returns(Path.of(resourcePath + "/references/metrics"), Bundle::absolutePath)
                                    .returns(Path.of("references/metrics"), Bundle::relativePath);

                            assertThat(metrics.markdownFiles()).hasSize(9);
                        }
                );
    }

    @Test
    @DisplayName("Loading google example bundle with configuration")
    void googleExamplesLoadingWithConfiguration() throws URISyntaxException {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing isRoot bundle =========================================================================================
        assertThat(bundle.rootBundle().childBundles()).hasSize(3);
    }

}
