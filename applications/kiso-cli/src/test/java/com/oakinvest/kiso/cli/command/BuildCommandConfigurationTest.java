package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.BaseTest;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class BuildCommandConfigurationTest extends BaseTest {

    @TempDir
    private Path temporaryDirectory;

    private static void assertMetaProperty(Document document, String property, String expected) {
        var meta = document.selectFirst("meta[property=%s]".formatted(property));
        assertThat(meta).isNotNull();
        assertThat(meta.attr("content")).isEqualTo(expected);
    }

    private static void assertMetaName(Document document, String name, String expected) {
        var meta = document.selectFirst("meta[name=%s]".formatted(name));
        assertThat(meta).isNotNull();
        assertThat(meta.attr("content")).isEqualTo(expected);
    }

    @Test
    @DisplayName("Building Google OKF bundle with configuration")
    void buildWithConfiguration() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        Path destinationDirectory = temporaryDirectory.resolve("public");

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();

        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Kiso-cli - Running build command")
                // Those files should exist
                .contains("HTML Generated for index.md")
                .contains("HTML Generated for datasets/index.md")
                // I asked to remove files in references/joins
                .doesNotContain("HTML Generated for references/joins/index.md")
                .doesNotContain("HTML Generated for references/joins/events___ads_clickstats.md")
                // I asked to remove day_count.md and not even_count.md
                .doesNotContain("HTML Generated for references/metrics/day_count.md")
                .contains("HTML Generated for references/metrics/event_count.md")
                .contains("Done!");

        // Checking that configuration is applied to the generated HTML files ==========================================
        assertThat(Files.readString(destinationDirectory.resolve("index.html"), UTF_8))
                .contains("data-theme=\"corporate\"")
                .contains("href=\"https://knowledge.angara.finance/assets/css/application.css\"")
                .contains("href=\"https://knowledge.angara.finance/datasets/index.html\"")
                .contains("lang=\"fr\"")
                .contains("<title>My Knowledge Base</title>")
                .contains("<meta name=\"description\" content=\"My knowledge base description\">");
        assertThat(Files.readString(destinationDirectory.resolve("references/metrics/event_count.html"), UTF_8))
                .contains("data-theme=\"corporate\"")
                .contains("href=\"https://knowledge.angara.finance/index.html\"")
                .contains("lang=\"fr\"")
                .contains("<title>Event Count</title>")
                .contains("<meta name=\"description\" content=\"Total number of events.\">");
        assertThat(Files.readString(destinationDirectory.resolve("sitemap.xml"), UTF_8))
                .contains("<loc>https://knowledge.angara.finance/index.html</loc>")
                .contains("<loc>https://knowledge.angara.finance/references/metrics/event_count.html</loc>");
        assertThat(Files.readString(destinationDirectory.resolve("llms.txt"), UTF_8))
                .contains("[index.md](https://knowledge.angara.finance/index.md)")
                .contains("[Event Count](https://knowledge.angara.finance/references/metrics/event_count.md)");

        // Checking that the social preview images are generated =======================================================
        assertThat(Files.exists(destinationDirectory.resolve("index.svg"))).isTrue();
        assertThat(Files.exists(destinationDirectory.resolve("index.png"))).isTrue();
        assertThat(Files.exists(destinationDirectory.resolve("references/metrics/event_count.svg"))).isTrue();
        assertThat(Files.exists(destinationDirectory.resolve("references/metrics/event_count.png"))).isTrue();

        // Checking the social share html tags =========================================================================
        assertThat(Files.readString(destinationDirectory.resolve("references/metrics/event_count.html"), UTF_8))
                .contains("<meta property=\"og:locale\" content=\"fr\">")
                .contains("<meta property=\"og:site_name\" content=\"Knowledge Base\">")
                .contains("<meta property=\"og:url\" content=\"https://knowledge.angara.finance/references/metrics/event_count.html\">")
                .contains("<meta property=\"og:type\" content=\"website\">")
                .contains("<meta property=\"og:title\" content=\"Event Count\">")
                .contains("<meta property=\"og:description\" content=\"Total number of events.\">")
                .contains("<meta property=\"og:image\" content=\"https://knowledge.angara.finance/references/metrics/event_count.html\">")
                .contains("<meta property=\"og:image:width\" content=\"1200\">")
                .contains("<meta property=\"og:image:height\" content=\"630\">")
                .contains("<meta name=\"twitter:card\" content=\"summary_large_image\">")
                .contains("<meta name=\"twitter:title\" content=\"Event Count\">")
                .contains("<meta name=\"twitter:description\" content=\"Total number of events.\">")
                .contains("<meta name=\"twitter:image\" content=\"https://knowledge.angara.finance/references/metrics/event_count.html\">");
    }

    @Test
    @DisplayName("Building Google OKF bundle with configuration profile")
    void buildWithConfigurationProfile() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        Path destinationDirectory = temporaryDirectory.resolve("public");

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();

        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", "valid-profile"
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Kiso-cli - Running build command")
                // Those files should exist
                .contains("HTML Generated for index.md")
                // I asked to remove datasets.
                .doesNotContain("HTML Generated for datasets/index.md")
                // I asked to remove files in references/joins
                .contains("HTML Generated for references/joins/index.md")
                .contains("HTML Generated for references/joins/events___ads_clickstats.md")
                // I asked to remove even_count.md and not day_count.md
                .contains("HTML Generated for references/metrics/day_count.md")
                .doesNotContain("HTML Generated for references/metrics/event_count.md")
                .contains("Done!");

        // Checking that configuration is applied to the generated HTML files ==========================================
        assertThat(Files.readString(destinationDirectory.resolve("index.html"), UTF_8))
                .contains("data-theme=\"pastel\"")
                .contains("lang=\"de\"")
                .contains("<title>My Knowledge Base 2</title>")
                .contains("<meta name=\"description\" content=\"My knowledge base description 3\">");
        assertThat(Files.readString(destinationDirectory.resolve("references/metrics/day_count.html"), UTF_8))
                .contains("data-theme=\"pastel\"")
                .contains("lang=\"de\"")
                .contains("<title>Day Count</title>")
                .contains("<meta name=\"description\" content=\"Total number of unique days.\">");
    }

    @Test
    @DisplayName("Building Google OKF bundle with blank profile")
    void buildWithBlankProfile() {
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        Path destinationDirectory = temporaryDirectory.resolve("blank-profile");

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", ""
                );

        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Kiso-cli - Running build command")
                .contains("HTML Generated for index.md")
                .contains("Done!");
    }

    @Test
    @DisplayName("Building Google OKF bundle with unknown profile")
    void buildWithUnknownProfile() {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        Path destinationDirectory = temporaryDirectory.resolve("public");

        // We execute code =============================================================================================
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", "unknown-profile"
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(error.toString()).contains("Error loading configuration: Profile does not exist");
    }

    @Test
    @DisplayName("Building Google OKF bundle with empty profile")
    void buildWithEmptyProfile() {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        Path destinationDirectory = temporaryDirectory.resolve("empty-profile");

        // We execute code =============================================================================================
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", "empty-profile"
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(error.toString()).contains("Error loading configuration: Profile does not exist");
    }

}
