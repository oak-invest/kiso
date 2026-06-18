package com.oakinvest.kiso.core.scan;

import com.oakinvest.kiso.core.model.bundle.BundleDirectory;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link KnowledgeBundleScanner}.
 */
class KnowledgeBundleScannerTest {

    /**
     * Test scan reads directories and Markdown files from a bundle.
     *
     * @throws URISyntaxException when the test resource cannot be converted to a path
     */
    @Test
    void scanShouldReadDirectoriesAndMarkdownFiles() throws URISyntaxException {
        Path sourceDirectory = testResourcePath("kb-google-example");

        KnowledgeBundle knowledgeBundle = new KnowledgeBundleScanner().scan(sourceDirectory);

        assertEquals(sourceDirectory.toAbsolutePath().normalize(), knowledgeBundle.rootDirectory());
        assertEquals(6, knowledgeBundle.directories().size());
        assertEquals(17, knowledgeBundle.markdownFiles().size());

        BundleDirectory rootDirectory = findDirectory(knowledgeBundle, "");
        assertEquals(List.of(Path.of("datasets"), Path.of("references"), Path.of("tables")),
                rootDirectory.childDirectories());
        assertEquals(List.of(Path.of("index.md")), rootDirectory.markdownFiles());

        BundleDirectory tablesDirectory = findDirectory(knowledgeBundle, "tables");
        assertEquals(List.of(), tablesDirectory.childDirectories());
        assertEquals(List.of(Path.of("tables/events_.md"), Path.of("tables/index.md")),
                tablesDirectory.markdownFiles());

        MarkdownFile rootIndexFile = findMarkdownFile(knowledgeBundle, "index.md");
        assertEquals(MarkdownFileKind.INDEX, rootIndexFile.kind());
        assertTrue(rootIndexFile.source().contains("# Subdirectories"));

        MarkdownFile eventsFile = findMarkdownFile(knowledgeBundle, "tables/events_.md");
        assertEquals(MarkdownFileKind.CONCEPT, eventsFile.kind());
        assertTrue(eventsFile.source().contains("type: BigQuery Table"));
        assertTrue(eventsFile.source().contains("# Schema"));
    }

    private Path testResourcePath(final String resourceName) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        return Path.of(resource.toURI());
    }

    private BundleDirectory findDirectory(final KnowledgeBundle knowledgeBundle, final String relativePath) {
        return knowledgeBundle.directories().stream()
                .filter(directory -> directory.relativePath().equals(Path.of(relativePath)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing directory: " + relativePath));
    }

    private MarkdownFile findMarkdownFile(final KnowledgeBundle knowledgeBundle, final String relativePath) {
        return knowledgeBundle.markdownFiles().stream()
                .filter(file -> file.relativePath().equals(Path.of(relativePath)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing Markdown file: " + relativePath));
    }

}
