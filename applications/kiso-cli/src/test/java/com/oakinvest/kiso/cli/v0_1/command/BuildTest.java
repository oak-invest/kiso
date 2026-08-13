package com.oakinvest.kiso.cli.v0_1.command;

import com.oakinvest.kiso.cli.command.BuildCommand;
import com.oakinvest.kiso.cli.util.BaseTest;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.oakinvest.kiso.core.util.FileConstants.LLMS_TXT_FILENAME;
import static com.oakinvest.kiso.core.util.FileConstants.SEARCH_INDEX_JSON_FILENAME;
import static com.oakinvest.kiso.core.util.FileConstants.SITEMAP_XML_FILENAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("vO.1 - Build command")
class BuildTest extends BaseTest {

    @Test
    @DisplayName("Build a valid OKF bundle")
    void buildValidBundle(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_V_0_1);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString(),
                        "--destination", temporaryDirectory.toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .doesNotContain("WARNING")
                .contains("Running build command")
                .contains("Done!");

        // Checking the zip files ======================================================================================
        Path rootZip = temporaryDirectory.resolve("bundle.zip");
        assertThat(rootZip).exists().isRegularFile();
        try (ZipFile zip = new ZipFile(rootZip.toFile())) {
            assertThat(zip.getFileHeaders())
                    .extracting(FileHeader::getFileName)
                    .containsExactlyInAnyOrder(
                            "datasets/ga4_obfuscated_sample_ecommerce.md",
                            "datasets/index.md",
                            "datasets/",
                            "references/metrics/avg_pageviews.md",
                            "references/metrics/new_user_count.md",
                            "references/metrics/user_count.md",
                            "references/metrics/day_count.md",
                            "references/metrics/event_count.md",
                            "references/metrics/avg_spend_per_purchase_session_by_user.md",
                            "references/metrics/index.md",
                            "references/metrics/overall_avg_spend_per_purchase_session.md",
                            "references/metrics/avg_transactions_per_purchaser.md",
                            "references/metrics/",
                            "references/index.md",
                            "references/joins/events___ads_clickstats.md",
                            "references/joins/index.md",
                            "references/joins/",
                            "references/",
                            "tables/index.md",
                            "tables/events_.md",
                            "tables/",
                            "tags/",
                            "tags/advanced-queries.md",
                            "tags/basic-queries.md",
                            "tags/bigquery.md",
                            "tags/ecommerce.md",
                            "tags/events.md",
                            "tags/google-ads.md",
                            "tags/google-analytics.md",
                            "tags/join.md",
                            "tags/metric.md",
                            "tags/public-dataset.md",
                            "tags/schema.md",
                            "tags/web-analytics.md",
                            "index.md"
                    );
        }

        Path datasetZip = temporaryDirectory.resolve("datasets/bundle.zip");
        assertThat(datasetZip).exists().isRegularFile();
        try (ZipFile zip = new ZipFile(datasetZip.toFile())) {
            assertThat(zip.getFileHeaders())
                    .extracting(FileHeader::getFileName)
                    .containsExactlyInAnyOrder(
                            "ga4_obfuscated_sample_ecommerce.md",
                            "index.md"
                    );
        }
    }

    @Test
    @DisplayName("Building a simple OKF bundle")
    void build(@TempDir Path temporaryDirectory) throws IOException {
        // What we are testing =========================================================================================
        var sourceDirectory = temporaryDirectory.resolve("source");
        var destinationDirectory = temporaryDirectory.resolve("public");
        Files.createDirectories(sourceDirectory.resolve("topics"));

        Files.writeString(sourceDirectory.resolve("index.md"), """
                        # Test bundle
                
                        - [topics](topics/index.md)
                """.stripIndent(), UTF_8);
        Files.writeString(sourceDirectory.resolve("topics/index.md"), """   
                        # Topics
                
                        - [First Topic](first-topic.md)
                """.stripIndent(), UTF_8);
        Files.writeString(sourceDirectory.resolve("topics/first-topic.md"), """
                ---
                type: Reference
                title: First Topic
                description: A first test topic.
                timestamp: '2026-06-24T10:00:00+00:00'
                ---
                
                # First Topic
                
                Hello from the first topic.
                """.stripIndent(), UTF_8);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Checking that the social preview images are generated =======================================================
        assertThat(Files.exists(destinationDirectory.resolve("index.png"))).isFalse();

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Running build command")
                .contains("HTML Generated for index.md")
                .contains("HTML Generated for topics/index.md")
                .contains("HTML Generated for topics/first-topic.md")
                .contains("File llms.txt generated")
                .contains("File sitemap.xml generated")
                .contains("File search-index.json generated")
                .contains("Done!");

        // Testing copied source files =================================================================================
        assertThat(destinationDirectory.resolve("index.md")).exists();
        assertThat(destinationDirectory.resolve("topics/index.md")).exists();
        assertThat(destinationDirectory.resolve("topics/first-topic.md")).exists();

        // Testing generated website files =============================================================================
        assertThat(destinationDirectory.resolve("index.html")).exists();
        assertThat(destinationDirectory.resolve("topics/index.html")).exists();
        assertThat(destinationDirectory.resolve("topics/first-topic.html")).exists();
        assertThat(destinationDirectory.resolve("assets/css/application.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/daisyui.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/themes.css")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/en.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/fr.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/de.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/es.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/it.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/pt.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/nl.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/pl.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ru.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/zh.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ja.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ko.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ar.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/hi.json")).exists();
        assertThat(destinationDirectory.resolve("assets/js/browser.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/minisearch.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/i18next.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-i18n.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-search.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-back-to-top.js")).exists();
        assertThat(destinationDirectory.resolve("search-index.json")).exists();

        // Testing an HTML file content ================================================================================
        var topicHtml = Files.readString(destinationDirectory.resolve("topics/first-topic.html"), UTF_8);
        assertThat(topicHtml)
                .contains("First Topic")
                .contains("A first test topic.")
                .contains("Hello from the first topic.")
                // Without a base URL, generated links must remain relative.
                .contains("href=\"../assets/css/application.css?build=")
                .contains("href=\"../index.html\"")
                .doesNotContain("https://knowledge.angara.finance");

        // Testing llms.txt file =======================================================================================
        assertThat(Files.readString(destinationDirectory.resolve(LLMS_TXT_FILENAME), UTF_8))
                .contains("# Knowledge Bundle")
                .contains("## index")
                .contains("- [index.md](index.md): Knowledge bundle index")
                .contains("## topics")
                .contains("- [index.md](topics/index.md): Index of topics")
                .contains("- [First Topic](topics/first-topic.md): A first test topic.")
                // We should not have the HTML assets directories in the llms.txt file.
                .doesNotContain("## assets")
                .doesNotContain("## assets/css")
                .doesNotContain("## assets/js");

        // Testing sitemap.xml file ====================================================================================
        assertThat(Files.readString(destinationDirectory.resolve(SITEMAP_XML_FILENAME), UTF_8))
                .contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
                .contains("<loc>index.html</loc>")
                .contains("<loc>topics/index.html</loc>")
                .contains("<loc>topics/first-topic.html</loc>")
                .contains("<lastmod>2026-06-24T10:00Z</lastmod>");

        // Testing search-index.json file ==============================================================================
        assertThat(Files.readString(destinationDirectory.resolve(SEARCH_INDEX_JSON_FILENAME), UTF_8))
                .contains("\"id\" : \"topics/first-topic\"")
                .contains("\"url\" : \"topics/first-topic.html\"")
                .contains("\"title\" : \"First Topic\"");
    }

    @Test
    @DisplayName("Building with error in the bundle")
    void buildWithErrorInTheBundle(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing =========================================================================================
        var sourceDirectory = temporaryDirectory.resolve("source");
        var destinationDirectory = temporaryDirectory.resolve("public");
        Files.createDirectories(sourceDirectory);
        Files.createDirectories(destinationDirectory);

        // We create a concept file without a frontmatter to trigger an error.
        var file1 = sourceDirectory.resolve("missing-frontmatter.md");
        Files.writeString(file1, "This file has no frontmatter.");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(output.toString())
                .contains("Running build command")
                .doesNotContain("Done!");
        assertThat(error.toString())
                .contains("ERROR - MISSING_FRONTMATTER - File missing-frontmatter.md is missing mandatory frontmatter");
    }

    @Test
    @DisplayName("Building an OKF bundle with existing assets")
    void buildWithExistingAssets() throws Exception {
        // What we are testing =========================================================================================
        var sourceDirectory = Paths.get(ClassLoader.getSystemResource("kb-with-assets").toURI());
        var destinationDirectory = Path.of("target", "test-kb-with-assets");
        Files.createDirectories(destinationDirectory);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Running build command")
                .contains("HTML Generated for index.md")
                .contains("File llms.txt generated")
                .contains("File sitemap.xml generated")
                .contains("File search-index.json generated")
                .contains("Done!");

        // Testing copied source files =================================================================================
        assertThat(destinationDirectory.resolve("index.html")).exists();
        assertThat(destinationDirectory.resolve("assets/css/application.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/daisyui.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/themes.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/test.css")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/en.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/fr.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/de.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/es.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/it.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/pt.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/nl.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/pl.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ru.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/zh.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ja.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ko.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/ar.json")).exists();
        assertThat(destinationDirectory.resolve("assets/i18n/hi.json")).exists();
        assertThat(destinationDirectory.resolve("assets/images/test.jpg")).exists();
        assertThat(destinationDirectory.resolve("assets/js/browser.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/minisearch.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/i18next.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-i18n.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-search.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/kiso-back-to-top.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/test.js")).exists();
        assertThat(destinationDirectory.resolve("search-index.json")).exists();
    }

    @Test
    @DisplayName("Build a bundle with broken links")
    void buildWithBrokenLinks(@TempDir Path temporaryDirectory) {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-with-broken-links");

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toAbsolutePath().toString(),
                        "--destination", temporaryDirectory.toString()
                );

        // Checking the results ========================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("WARNING - BROKEN_LINK - File index.md contains broken link: uknownContent.md")
                .contains("Running build command")
                .contains("Done!");
    }

}
