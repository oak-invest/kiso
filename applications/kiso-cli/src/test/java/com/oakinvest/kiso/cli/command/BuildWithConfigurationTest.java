package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.util.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.FileConstants.DEFAULT_DESTINATION_DIRECTORY_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Build command with configuration")
class BuildWithConfigurationTest extends BaseTest {

    @Test
    @DisplayName("Building Google OKF bundle with configuration")
    void buildWithConfiguration(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing : The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
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
                .contains("Running build command")
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
                .contains("href=\"https://knowledge.angara.finance/assets/css/application.css?build=")
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
                .contains("<meta property=\"og:image\" content=\"https://knowledge.angara.finance/references/metrics/event_count.png\">")
                .contains("<meta property=\"og:image:width\" content=\"1200\">")
                .contains("<meta property=\"og:image:height\" content=\"630\">")
                .contains("<meta name=\"twitter:card\" content=\"summary_large_image\">")
                .contains("<meta name=\"twitter:title\" content=\"Event Count\">")
                .contains("<meta name=\"twitter:description\" content=\"Total number of events.\">")
                .contains("<meta name=\"twitter:image\" content=\"https://knowledge.angara.finance/references/metrics/event_count.png\">");
    }

    @Test
    @DisplayName("Building Google OKF bundle with configuration profile")
    void buildWithConfigurationProfile(@TempDir Path temporaryDirectory) throws Exception {
        // What we are testing : The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
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
                .contains("Running build command")
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
    void buildWithBlankProfile(@TempDir Path temporaryDirectory) {
        // What we are testing : The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute the command ======================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", resourcePath.toString(),
                        "--destination", destinationDirectory.toString(),
                        "--profile", ""
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isZero();
        assertThat(error.toString()).isEmpty();
        assertThat(output.toString())
                .contains("Running build command")
                .contains("HTML Generated for index.md")
                .contains("Done!");
    }

    @Test
    @DisplayName("Building Google OKF bundle with unknown profile")
    void buildWithUnknownProfile(@TempDir Path temporaryDirectory) {
        // What we are testing : The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute code =============================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
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
    void buildWithEmptyProfile(@TempDir Path temporaryDirectory) {
        // What we are testing : The Google example ====================================================================
        var resourcePath = getResourcePath(KB_GOOGLE_WITH_CONFIGURATION);
        var destinationDirectory = temporaryDirectory.resolve(DEFAULT_DESTINATION_DIRECTORY_NAME);

        // We execute code =============================================================================================
        var output = new StringWriter();
        var error = new StringWriter();
        var exitCode = new CommandLine(new BuildCommand())
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
