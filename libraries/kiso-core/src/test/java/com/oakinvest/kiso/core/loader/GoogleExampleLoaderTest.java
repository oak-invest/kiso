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
 * ├── datasets
 * │ ├── ga4_obfuscated_sample_ecommerce.md
 * │ └── index.md
 * ├── index.md
 * ├── references
 * │ ├── index.md
 * │ ├── joins
 * │ │ ├── events___ads_clickstats.md
 * │ │ └── index.md
 * │ └── metrics
 * │     ├── avg_pageviews.md
 * │     ├── avg_spend_per_purchase_session_by_user.md
 * │     ├── avg_transactions_per_purchaser.md
 * │     ├── day_count.md
 * │     ├── event_count.md
 * │     ├── index.md
 * │     ├── new_user_count.md
 * │     ├── overall_avg_spend_per_purchase_session.md
 * │     └── user_count.md
 * └── tables
 * ├── events_.md
 * └── index.md
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

        // "datasets" bundle ===========================================================================================
        var datasetBundle = bundle.rootBundle().childBundleDirectories().getFirst();
        assertThat(datasetBundle)
                .returns(Path.of(resourcePath + "/datasets"), Bundle::path)
                .returns(Path.of("datasets"), Bundle::relativePath)
                .returns(List.of(), Bundle::childBundleDirectories);

        assertThat(datasetBundle.markdownFiles()).hasSize(2)
                .satisfiesExactly(
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
        assertThat(bundle.rootBundle().childBundleDirectories().get(1))
                .satisfies(references -> {
                    assertThat(references.path()).isEqualTo(Path.of(resourcePath + "/references"));
                    assertThat(references.relativePath()).isEqualTo(Path.of("references"));
                    assertThat(references.childBundleDirectories()).hasSize(2);
                    assertThat(references.markdownFiles()).hasSize(1);
                });


        assertThat(bundle.rootBundle().childBundleDirectories())
                .satisfiesExactly(
                        // Datasets directory ==========================================================================
                        datasets -> {
                            assertThat(datasets.path()).isEqualTo(Path.of(resourcePath + "/datasets"));
                            assertThat(datasets.relativePath()).isEqualTo(Path.of("datasets"));
                            assertThat(datasets.childBundleDirectories()).isEmpty();
                            assertThat(datasets.markdownFiles()).hasSize(2);

                        },
                        // References directory ========================================================================
                        references -> {

                        },
                        // Tables directory ============================================================================
                        tables -> {

                        }
                );
//                .extracting(
//                        Bundle::path,
//                        Bundle::relativePath,
//                        Bundle::childBundleDirectories,
//                        Bundle::markdownFiles
//                )
//                .contains(
//                        // Datasets
//                        tuple(
//                                Path.of(resourcePath + "/datasets"),
//                                Path.of("datasets"),
//
//                        )
//                );

        assertThat(bundle.rootBundle().markdownFiles()).hasSize(1);


        //Path sourceDirectory = ;


        //
//        KnowledgeBundle bundle = new KnowledgeBundleLoader().load(sourceDirectory);
//
//        assertEquals(sourceDirectory.toAbsolutePath().normalize(), bundle.rootBundlePath());
//        assertEquals(6, allDirectories(bundle).size());
//        Bundle rootDirectory = bundle.rootBundle();
//        assertEquals(List.of(Path.of("datasets"), Path.of("references"), Path.of("tables")),
//                rootDirectory.childBundleDirectories().stream()
//                        .map(Bundle::relativePath)
//                        .toList());
//        assertEquals(List.of(Path.of("index.md")), rootDirectory.markdownFiles().stream()
//                .map(MarkdownFile::relativePath)
//                .toList());
//
//        Bundle tablesDirectory = findDirectory(bundle, "tables");
//        assertEquals(List.of(), tablesDirectory.childBundleDirectories());
//        assertEquals(List.of(Path.of("tables/events_.md"), Path.of("tables/index.md")),
//                tablesDirectory.markdownFiles().stream()
//                        .map(MarkdownFile::relativePath)
//                        .toList());
//
//        MarkdownFile rootIndexFile = findMarkdownFile(bundle, "index.md");
//        assertEquals(MarkdownFileKind.INDEX, rootIndexFile.kind());
//        assertTrue(rootIndexFile.content().contains("# Subdirectories"));
//
//        MarkdownFile eventsFile = findMarkdownFile(bundle, "tables/events_.md");
//        assertEquals(MarkdownFileKind.CONCEPT, eventsFile.kind());
//        assertTrue(eventsFile.content().contains("type: BigQuery Table"));
//        assertTrue(eventsFile.content().contains("# Schema"));
    }

}
