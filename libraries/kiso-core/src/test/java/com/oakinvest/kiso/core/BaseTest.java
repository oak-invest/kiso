package com.oakinvest.kiso.core;

import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Base test class.
 */
public class BaseTest {

    /** Google example knowledge base v0.1. */
    public static final String KB_GOOGLE_V_0_1 = "v0.1/kb-google-example";

    /** Google example knowledge base with configuration v0.1. */
    public static final String KB_GOOGLE_V_0_1_WITH_CONFIGURATION = "v0.1/kb-google-example-with-configuration";

    /** Google example knowledge base v0.2. */
    public static final String KB_GOOGLE_V_0_2 = "v0.2/kb-google-example";

    /** Google example knowledge base with configuration v0.2. */
    public static final String KB_GOOGLE_V_0_2_WITH_CONFIGURATION = "v0.2/kb-google-example-with-configuration";

    /** Acme example knowledge base v0.2. */
    public static final String KB_ACME_V_0_2 = "v0.2/kb-acme-example";

    protected Path getResourcePath(final String resourceName) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected void createMarkdownFile(
            final Path rootDirectory,
            final String relativePath,
            final String content
    ) throws IOException {
        final Path markdownFile = rootDirectory.resolve(relativePath);
        Files.createDirectories(markdownFile.getParent());
        Files.writeString(markdownFile, content);
    }

    protected MarkdownFile createMarkdownFile(
            final Path directory,
            final String frontmatter
    ) throws IOException {
        Files.writeString(directory.resolve("concept.md"), """
                ---
                %s
                ---
                
                # Concept
                
                """.formatted(frontmatter));
        return KnowledgeBundleLoader.load(directory)
                .rootBundle()
                .markdownFiles()
                .getFirst();
    }

    protected MarkdownFile createMarkdownFile(
            final Path markdownFilePath,
            final MarkdownFileKind kind,
            final Frontmatter frontmatter
    ) {
        return MarkdownFile.builder()
                .fileName(markdownFilePath.getFileName().toString())
                .kind(kind)
                .absolutePath(markdownFilePath)
                .relativePath(markdownFilePath)
                .frontmatter(frontmatter)
                .frontmatterPresent(frontmatter != null)
                .build();
    }

    protected Bundle createBundleWith(final MarkdownFile markdownFile) {
        return createBundleWith(List.of(markdownFile));
    }

    protected Bundle createBundleWith(final List<MarkdownFile> markdownFiles) {
        return Bundle.builder()
                .childBundles(List.of())
                .markdownFiles(markdownFiles)
                .build();
    }

}
