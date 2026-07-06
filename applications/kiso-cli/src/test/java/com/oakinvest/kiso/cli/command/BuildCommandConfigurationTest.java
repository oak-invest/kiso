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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class BuildCommandConfigurationTest extends BaseTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Building Google OKF bundle with configuration")
    void buildWithConfiguration() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
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
                .contains("lang=\"fr\"")
                .contains("<title>My Knowledge Base</title>")
                .contains("<meta name=\"description\" content=\"My knowledge base description\">");
        assertThat(Files.readString(destinationDirectory.resolve("references/metrics/event_count.html"), UTF_8))
                .contains("data-theme=\"corporate\"")
                .contains("lang=\"fr\"")
                .contains("<title>Event Count</title>")
                .contains("<meta name=\"description\" content=\"Total number of events.\">");
    }

    @Test
    @DisplayName("Building Google OKF bundle with configuration profile")
    void buildWithConfigurationProfile() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
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
    @DisplayName("Building Google OKF bundle with unknown profile")
    void buildWithUnknownProfile() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
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
        assertThat(error.toString())
                .contains("Error loading configuration: Profile does not exist");
    }

    @Test
    @DisplayName("Building Google OKF bundle with empty profile")
    void buildWithEmptyProfile() throws Exception {
        // What we are testing - The Google example ====================================================================
        var resourcePath = getResourcePath("kb-google-example-v0.1-with-configuration");
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
        assertThat(error.toString())
                .contains("Error loading configuration: Profile does not exist");
    }

}
