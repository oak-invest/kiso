package com.oakinvest.kiso.core.v0_2.loader;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.Generated;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.2 - Loading google example bundle")
class GoogleExampleLoadingTest extends BaseTest {

    @Test
    @DisplayName("Loading google example bundle without configuration")
    void googleExamplesLoadingWithoutConfiguration() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing isRoot bundle =======================================================================================
        assertThat(bundle.rootBundle().childBundles()).hasSize(3);
        assertThat(bundle.rootBundle().markdownFiles())
                .hasSize(1)
                // index ===============================================================================================
                .satisfiesExactly(index -> {
                    // File information.
                    assertThat(index.fileName()).isEqualTo("index.md");
                    assertThat(index.conceptId()).isNull();
                    assertThat(index.kind()).isEqualTo(INDEX);

                    // Bundle information.
                    assertThat(index.bundlePath()).isEmpty();
                    assertThat(index.bundleName()).isEqualTo("index");

                    // Path.
                    assertThat(index.absolutePath()).isEqualTo(Path.of(resourcePath + "/index.md"));
                    assertThat(index.relativePath()).isEqualTo(Path.of("index.md"));

                    // HTML.
                    assertThat(index.htmlFilename()).isEqualTo("index.html");
                    assertThat(index.htmlFilePath()).isEqualTo("index.html");

                    // Frontmatter.
                    assertThat(index.frontmatterPresent()).isFalse();

                    // Content.
                    assertThat(index.body()).contains("# Subdirectories");
                });

        // "datasets" bundle ===========================================================================================
        var datasetBundle = bundle.rootBundle().childBundles().getFirst();
        assertThat(datasetBundle)
                .returns("datasets", Bundle::name)
                .returns(Path.of(resourcePath + "/datasets"), Bundle::absolutePath)
                .returns(Path.of("datasets"), Bundle::relativePath)
                .returns("datasets", Bundle::simpleName)
                .returns(List.of(), Bundle::childBundles);

        assertThat(datasetBundle.markdownFiles())
                .hasSize(2)
                .satisfiesExactly(
                        // ga4 =========================================================================================
                        // /datasets/ga4_obfuscated_sample_ecommerce.md
                        ga4 -> {
                            // File information.
                            assertThat(ga4.fileName()).isEqualTo("ga4_obfuscated_sample_ecommerce.md");
                            assertThat(ga4.conceptId()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce");
                            assertThat(ga4.kind()).isEqualTo(CONCEPT);

                            // Bundle information.
                            assertThat(ga4.bundlePath()).isEqualTo("datasets");
                            assertThat(ga4.bundleName()).isEqualTo("datasets");

                            // Path.
                            assertThat(ga4.absolutePath()).isEqualTo(Path.of(resourcePath + "/datasets/ga4_obfuscated_sample_ecommerce.md"));
                            assertThat(ga4.relativePath()).isEqualTo(Path.of("datasets/ga4_obfuscated_sample_ecommerce.md"));

                            // HTML.
                            assertThat(ga4.htmlFilename()).isEqualTo("ga4_obfuscated_sample_ecommerce.html");
                            assertThat(ga4.htmlFilePath()).isEqualTo("datasets/ga4_obfuscated_sample_ecommerce.html");

                            // Frontmatter.
                            assertThat(ga4.frontmatter())
                                    .isNotNull()
                                    .returns("BigQuery Dataset", Frontmatter::type)
                                    .returns("https://bigquery.googleapis.com/v2/projects/bigquery-public-data/datasets/ga4_obfuscated_sample_ecommerce", Frontmatter::resource)
                                    .returns("GA4 Obfuscated Sample Ecommerce Dataset", Frontmatter::title)
                                    .returns("Obfuscated Google Analytics 4 dataset emulating a web ecommerce implementation of the Google Merchandise Store.", Frontmatter::description)
                                    .returns(List.of("ga4", "ecommerce", "obfuscated", "analytics", "sample-data"), Frontmatter::tags);
                            assertThat(ga4.frontmatter().generated())
                                    .isNotNull()
                                    .returns("reference_agent/gemini-3.5-flash", Generated::by)
                                    .returns("2026-07-10T21:14:56+00:00", Generated::at)
                                    .returns(OffsetDateTime.parse("2026-07-10T21:14:56+00:00"), Generated::parsedAt);
                            assertThat(ga4.timestamp()).isEqualTo(OffsetDateTime.parse("2026-07-10T21:14:56+00:00"));

                            // Content.
                            assertThat(ga4.body()).doesNotContain("type: BigQuery Dataset");
                            assertThat(ga4.body()).contains("This dataset contains a single sharded table family");
                        },
                        // index =======================================================================================
                        // /datasets/index
                        index -> {
                            // File information.
                            assertThat(index.fileName()).isEqualTo("index.md");
                            assertThat(index.kind()).isEqualTo(INDEX);

                            // Bundle information.
                            assertThat(index.bundlePath()).isEqualTo("datasets");
                            assertThat(index.bundleName()).isEqualTo("datasets");

                            // Path.
                            assertThat(index.absolutePath()).isEqualTo(Path.of(resourcePath + "/datasets/index.md"));
                            assertThat(index.relativePath()).isEqualTo(Path.of("datasets/index.md"));

                            // HTML.
                            assertThat(index.htmlFilename()).isEqualTo("index.html");
                            assertThat(index.htmlFilePath()).isEqualTo("datasets/index.html");

                            // Frontmatter.
                            assertThat(index.frontmatterPresent()).isFalse();

                            // Content.
                            assertThat(index.body()).contains("# BigQuery Dataset");
                        }
                );

        // "references" bundle =========================================================================================
        // /references
        var referencesBundle = bundle.rootBundle().childBundles().get(1);
        assertThat(referencesBundle)
                .returns("references", Bundle::name)
                .returns(Path.of(resourcePath + "/references"), Bundle::absolutePath)
                .returns(Path.of("references"), Bundle::relativePath)
                .returns("references", Bundle::simpleName);

        assertThat(referencesBundle.childBundles())
                .hasSize(1)
                .satisfiesExactly(
                        // "metrics" bundle ==============================================================================
                        // /references/metrics
                        metrics -> {
                            assertThat(metrics)
                                    .returns("references/metrics", Bundle::name)
                                    .returns(Path.of(resourcePath + "/references/metrics"), Bundle::absolutePath)
                                    .returns(Path.of("references/metrics"), Bundle::relativePath);

                            assertThat(metrics.markdownFiles())
                                    .hasSize(8)
                                    .first()
                                    .satisfies(
                                            acquiredUsers -> {
                                                assertThat(acquiredUsers)
                                                        .returns("acquired_users.md", MarkdownFile::fileName)
                                                        .returns("references/metrics/acquired_users", MarkdownFile::conceptId)
                                                        .returns(CONCEPT, MarkdownFile::kind)
                                                        .returns("references/metrics", MarkdownFile::bundlePath)
                                                        .returns("metrics", MarkdownFile::bundleName)
                                                        .returns(Path.of(resourcePath + "/references/metrics/acquired_users.md"), MarkdownFile::absolutePath)
                                                        .returns(Path.of("references/metrics/acquired_users.md"), MarkdownFile::relativePath)
                                                        .returns("acquired_users.html", MarkdownFile::htmlFilename)
                                                        .returns("references/metrics/acquired_users.html", MarkdownFile::htmlFilePath);
                                            }
                                    );
                        }
                );
    }

    @Test
    @DisplayName("Loading google example bundle with configuration")
    void googleExamplesLoadingWithConfiguration() {
        // What we are testing =========================================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_2_WITH_CONFIGURATION);
        var bundle = KnowledgeBundleLoader.load(resourcePath);

        // Testing root bundle =========================================================================================
        assertThat(bundle.rootBundle().childBundles()).hasSize(3);
    }

    @Test
    @DisplayName("Loading inline generated frontmatter")
    void inlineGeneratedFrontmatter(@TempDir Path temporaryDirectory) throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), """
                ---
                type: Concept
                generated: { by: reference_agent/gemini-2.5-pro, at: 2026-06-20T22:53:05Z }
                ---
                # Concept
                """);

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);
        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();

        assertThat(markdownFile.frontmatter().generated())
                .isNotNull()
                .returns("reference_agent/gemini-2.5-pro", Generated::by)
                .returns("2026-06-20T22:53:05Z", Generated::at)
                .returns(OffsetDateTime.parse("2026-06-20T22:53:05Z"), Generated::parsedAt);
        assertThat(markdownFile.timestamp()).isEqualTo(OffsetDateTime.parse("2026-06-20T22:53:05Z"));
    }

}
