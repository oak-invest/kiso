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

    /** Knowledge base Google example directory. */
    public static final String KB_GOOGLE_EXAMPLE_DIRECTORY = "kb-google-example-v0.1";

    /**
     * Retrieves absolutePath from a resource fileName.
     *
     * @param resourceName resource fileName
     * @return absolutePath
     * @throws URISyntaxException syntax problem with URI
     */
    @SuppressWarnings("SameParameterValue")
    protected Path getResourcePath(final String resourceName) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        return Path.of(resource.toURI());
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
