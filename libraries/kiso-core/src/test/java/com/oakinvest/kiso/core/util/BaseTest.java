package com.oakinvest.kiso.core.util;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Base test class.
 */
public class BaseTest {

    /** Google example knowledge base. */
    public static final String KB_GOOGLE = "kb-google-example-v0.1";

    /** Google example knowledge base with configuration. */
    public static final String KB_GOOGLE_WITH_CONFIGURATION = "kb-google-example-v0.1-with-configuration";

    protected Path getResourcePath(final String resourceName) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected MarkdownFile markdownFile(
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

    protected Bundle bundleWith(final MarkdownFile markdownFile) {
        return bundleWith(List.of(markdownFile));
    }

    protected Bundle bundleWith(final List<MarkdownFile> markdownFiles) {
        return Bundle.builder()
                .childBundles(List.of())
                .markdownFiles(markdownFiles)
                .build();
    }

}
