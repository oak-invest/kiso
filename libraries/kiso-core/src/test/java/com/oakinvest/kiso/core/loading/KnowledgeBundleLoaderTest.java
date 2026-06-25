package com.oakinvest.kiso.core.loading;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeBundleLoaderTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    @DisplayName("Load frontmatter closed at end of file")
    void loadFrontmatterClosedAtEndOfFile() throws IOException {
        Files.writeString(temporaryDirectory.resolve("concept.md"), "---\ntitle: Example\n---");

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.frontmatter().title()).isEqualTo("Example");
        assertThat(markdownFile.content()).isEmpty();
    }

    @Test
    @DisplayName("Ignore frontmatter with invalid closing delimiter")
    void ignoreFrontmatterWithInvalidClosingDelimiter() throws IOException {
        String content = "---\ntitle: Ignored\n---not-a-delimiter\n# Body";
        Files.writeString(temporaryDirectory.resolve("concept.md"), content);

        var bundle = KnowledgeBundleLoader.load(temporaryDirectory);

        var markdownFile = bundle.rootBundle().markdownFiles().getFirst();
        assertThat(markdownFile.frontmatter().title()).isNull();
        assertThat(markdownFile.content()).isEqualTo(content);
    }

}
