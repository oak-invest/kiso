package com.oakinvest.kiso.core.loader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeBundleLoaderTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Load tags from an inline YAML list")
    void loadTagsFromInlineYamlList() throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), """
                ---
                type: Presentation
                tags: [company, scub, presentation, fr]
                ---
                """);

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.frontmatter().tags())
                .isEqualTo(List.of("company", "scub", "presentation", "fr"));
    }

    @Test
    @DisplayName("Load tags from a block YAML list")
    void loadTagsFromBlockYamlList() throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), """
                ---
                type: Presentation
                tags:
                - company
                - scub
                - presentation
                - fr
                ---
                """);

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.frontmatter().tags())
                .isEqualTo(List.of("company", "scub", "presentation", "fr"));
    }

    @Test
    @DisplayName("Load frontmatter closed at end of file")
    void loadFrontmatterClosedAtEndOfFile() throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), "---\ntitle: Example\n---");

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.frontmatter().title()).isEqualTo("Example");
        assertThat(markdownFile.body()).isEmpty();
    }

    @Test
    @DisplayName("Ignore frontmatter with invalid closing delimiter")
    void ignoreFrontmatterWithInvalidClosingDelimiter() throws IOException {
        String content = "---\ntitle: Ignored\n---not-a-delimiter\n# Body";
        Files.writeString(temporaryDirectory.resolve("concept.md"), content);

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.hasFrontmatter()).isFalse();
        assertThat(markdownFile.body()).isEqualTo(content);
    }

}
