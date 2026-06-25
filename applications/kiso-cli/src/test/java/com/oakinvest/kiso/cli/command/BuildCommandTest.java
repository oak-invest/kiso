package com.oakinvest.kiso.cli.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BuildCommandTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Building an OKF bundle")
    void build() throws Exception {
        // What we are testing =========================================================================================
        Path sourceDirectory = temporaryDirectory.resolve("source");
        Path destinationDirectory = temporaryDirectory.resolve("public");
        createKnowledgeBundle(sourceDirectory);

        StringWriter output = new StringWriter();
        StringWriter error = new StringWriter();
        int exitCode = new CommandLine(new BuildCommand())
                .setOut(new PrintWriter(output))
                .setErr(new PrintWriter(error))
                .execute(
                        "--source", sourceDirectory.toString(),
                        "--destination", destinationDirectory.toString()
                );

        // Testing command result =====================================================================================
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

        // Testing copied source files ================================================================================
        assertThat(destinationDirectory.resolve("index.md")).exists();
        assertThat(destinationDirectory.resolve("topics/index.md")).exists();
        assertThat(destinationDirectory.resolve("topics/first-topic.md")).exists();

        // Testing generated website files ============================================================================
        assertThat(destinationDirectory.resolve("index.html")).exists();
        assertThat(destinationDirectory.resolve("topics/index.html")).exists();
        assertThat(destinationDirectory.resolve("topics/first-topic.html")).exists();
        assertThat(destinationDirectory.resolve("assets/css/application.css")).exists();
        assertThat(destinationDirectory.resolve("assets/js/browser@4.js")).exists();

        String indexHtml = Files.readString(destinationDirectory.resolve("topics/first-topic.html"), StandardCharsets.UTF_8);
        assertThat(indexHtml)
                .contains("First Topic")
                .contains("A first test topic.")
                .contains("Hello from the first topic.");

        // Testing generated agent files ==============================================================================
        String llmsTxt = Files.readString(destinationDirectory.resolve("llms.txt"), StandardCharsets.UTF_8);
        assertThat(llmsTxt)
                .contains("# Knowledge Bundle")
                .contains("## Index")
                .contains("- [index.md](index.md): Knowledge bundle index")
                .contains("## topics")
                .contains("- [index.md](topics/index.md): Index of topics")
                .contains("- [First Topic](topics/first-topic.md): A first test topic.");

        String sitemapXml = Files.readString(destinationDirectory.resolve("sitemap.xml"), StandardCharsets.UTF_8);
        assertThat(sitemapXml)
                .contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
                .contains("<loc>index.html</loc>")
                .contains("<loc>topics/index.html</loc>")
                .contains("<loc>topics/first-topic.html</loc>")
                .contains("<lastmod>2026-06-24T10:00Z</lastmod>");
    }

    /**
     * Creates a minimal OKF bundle for build tests.
     *
     * @param sourceDirectory source directory
     * @throws Exception if files cannot be created
     */
    private void createKnowledgeBundle(final Path sourceDirectory) throws Exception {
        Files.createDirectories(sourceDirectory.resolve("topics"));
        Files.writeString(
                sourceDirectory.resolve("index.md"),
                """
                        # Test bundle
                        
                        - [topics](topics/index.md)
                        """.stripIndent(),
                StandardCharsets.UTF_8
        );
        Files.writeString(
                sourceDirectory.resolve("topics/index.md"),
                """
                        # Topics
                        
                        - [First Topic](first-topic.md)
                        """.stripIndent(),
                StandardCharsets.UTF_8
        );
        Files.writeString(
                sourceDirectory.resolve("topics/first-topic.md"),
                """
                        ---
                        type: Reference
                        title: First Topic
                        description: A first test topic.
                        timestamp: '2026-06-24T10:00:00+00:00'
                        ---
                        
                        # First Topic
                        
                        Hello from the first topic.
                        """.stripIndent(),
                StandardCharsets.UTF_8
        );
    }

}
