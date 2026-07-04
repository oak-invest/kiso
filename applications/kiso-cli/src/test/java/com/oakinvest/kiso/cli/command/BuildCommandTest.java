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
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class BuildCommandTest extends BaseTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Building a simple OKF bundle")
    void build() throws Exception {
        // What we are testing =========================================================================================
        Path sourceDirectory = temporaryDirectory.resolve("source");
        Path destinationDirectory = temporaryDirectory.resolve("public");
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

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();

        int exitCode = new CommandLine(new BuildCommand())
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
                .contains("Kiso-cli - Running build command")
                .contains("HTML Generated for index.md")
                .contains("HTML Generated for topics/index.md")
                .contains("HTML Generated for topics/first-topic.md")
                .contains("File llms.txt generated")
                .contains("File sitemap.xml generated")
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
        assertThat(destinationDirectory.resolve("assets/css/daisyui@5.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/themes.css")).exists();
        assertThat(destinationDirectory.resolve("assets/js/browser@4.js")).exists();

        String indexHtml = Files.readString(destinationDirectory.resolve("topics/first-topic.html"), UTF_8);
        assertThat(indexHtml)
                .contains("First Topic")
                .contains("A first test topic.")
                .contains("Hello from the first topic.");

        // Testing generated agent files ===============================================================================
        String llmsTxt = Files.readString(destinationDirectory.resolve("llms.txt"), UTF_8);
        assertThat(llmsTxt)
                .contains("# Knowledge Bundle")
                .contains("## Index")
                .contains("- [index.md](index.md): Knowledge bundle index")
                .contains("## topics")
                .contains("- [index.md](topics/index.md): Index of topics")
                .contains("- [First Topic](topics/first-topic.md): A first test topic.");

        // We should not have the HTML assets directories in the llms.txt file.
        assertThat(llmsTxt).doesNotContain("## assets")
                .doesNotContain("## assets/css")
                .doesNotContain("## assets/js");

        String sitemapXml = Files.readString(destinationDirectory.resolve("sitemap.xml"), UTF_8);
        assertThat(sitemapXml)
                .contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
                .contains("<loc>index.html</loc>")
                .contains("<loc>topics/index.html</loc>")
                .contains("<loc>topics/first-topic.html</loc>")
                .contains("<lastmod>2026-06-24T10:00Z</lastmod>");
    }

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
    }

    @Test
    @DisplayName("Building with error in the bundle")
    void buildWithErrorInTheBundle() throws Exception {
        // What we are testing =========================================================================================
        Path sourceDirectory = temporaryDirectory.resolve("source");
        Path destinationDirectory = temporaryDirectory.resolve("public");
        Files.createDirectories(sourceDirectory);
        Files.createDirectories(destinationDirectory);

        // A file with error!
        Path file1 = sourceDirectory.resolve("missing-frontmatter.md");
        Files.writeString(file1, "This file has no frontmatter.");

        // Executing the build command =================================================================================
        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result ======================================================================================
        assertThat(exitCode).isNotZero();
        assertThat(output.toString())
                .contains("Kiso-cli - Running build command")
                .doesNotContain("Done!");
        assertThat(error.toString())
                .contains("ERROR - MISSING_FRONTMATTER - File missing-frontmatter.md is missing mandatory frontmatter");
    }

    @Test
    @DisplayName("Building an OKF bundle with assets")
    void buildWithAssets() throws Exception {
        // What we are testing =========================================================================================
        Path sourceDirectory = Paths.get(ClassLoader.getSystemResource("kb-with-assets").toURI());
        Path destinationDirectory = Path.of("target", "test-kb-with-assets");
        Files.createDirectories(destinationDirectory);

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
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
                .contains("Kiso-cli - Running build command")
                .contains("HTML Generated for index.md")
                .contains("File llms.txt generated")
                .contains("File sitemap.xml generated")
                .contains("Done!");

        // Testing copied source files =================================================================================
        assertThat(destinationDirectory.resolve("index.html")).exists();
        assertThat(destinationDirectory.resolve("assets/css/application.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/daisyui@5.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/themes.css")).exists();
        assertThat(destinationDirectory.resolve("assets/css/test.css")).exists();
        assertThat(destinationDirectory.resolve("assets/images/test.jpg")).exists();
        assertThat(destinationDirectory.resolve("assets/js/browser@4.js")).exists();
        assertThat(destinationDirectory.resolve("assets/js/test.js")).exists();
    }

}
