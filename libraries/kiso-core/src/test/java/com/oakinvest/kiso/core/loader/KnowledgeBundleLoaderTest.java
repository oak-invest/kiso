package com.oakinvest.kiso.core.loader;

import com.oakinvest.kiso.core.model.bundle.Bundle;
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
 * Tests for {@link KnowledgeBundleLoader}.
 */
class KnowledgeBundleLoaderTest {

    @Test
    void loadShouldReadDirectoriesAndMarkdownFiles() throws URISyntaxException {
        Path sourceDirectory = testResourcePath("kb-google-example");

        KnowledgeBundle bundle = new KnowledgeBundleLoader().loadKnowledgeBundle(sourceDirectory);

        assertEquals(sourceDirectory.toAbsolutePath().normalize(), bundle.rootBundlePath());
        assertEquals(6, allDirectories(bundle).size());
        Bundle rootDirectory = bundle.rootBundle();
        assertEquals(List.of(Path.of("datasets"), Path.of("references"), Path.of("tables")),
                rootDirectory.childBundleDirectories().stream()
                        .map(Bundle::relativePath)
                        .toList());
        assertEquals(List.of(Path.of("index.md")), rootDirectory.markdownFiles().stream()
                .map(MarkdownFile::relativePath)
                .toList());

        Bundle tablesDirectory = findDirectory(bundle, "tables");
        assertEquals(List.of(), tablesDirectory.childBundleDirectories());
        assertEquals(List.of(Path.of("tables/events_.md"), Path.of("tables/index.md")),
                tablesDirectory.markdownFiles().stream()
                        .map(MarkdownFile::relativePath)
                        .toList());

        MarkdownFile rootIndexFile = findMarkdownFile(bundle, "index.md");
        assertEquals(MarkdownFileKind.INDEX, rootIndexFile.kind());
        assertTrue(rootIndexFile.content().contains("# Subdirectories"));

        MarkdownFile eventsFile = findMarkdownFile(bundle, "tables/events_.md");
        assertEquals(MarkdownFileKind.CONCEPT, eventsFile.kind());
        assertTrue(eventsFile.content().contains("type: BigQuery Table"));
        assertTrue(eventsFile.content().contains("# Schema"));
    }

    private Path testResourcePath(final String resourceName) throws URISyntaxException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Missing test resource: " + resourceName);
        return Path.of(resource.toURI());
    }

    private List<Bundle> allDirectories(final KnowledgeBundle bundle) {
        return allDirectories(bundle.rootBundle());
    }

    private List<Bundle> allDirectories(final Bundle rootDirectory) {
        List<Bundle> directories = new java.util.ArrayList<>();
        addDirectoryAndChildren(rootDirectory, directories);
        return List.copyOf(directories);
    }

    private void addDirectoryAndChildren(
            final Bundle directory,
            final List<Bundle> directories
    ) {
        directories.add(directory);
        directory.childBundleDirectories().forEach(childDirectory -> addDirectoryAndChildren(childDirectory, directories));
    }

    private Bundle findDirectory(final KnowledgeBundle bundle, final String relativePath) {
        return allDirectories(bundle).stream()
                .filter(directory -> directory.relativePath().equals(Path.of(relativePath)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing directory: " + relativePath));
    }

    private MarkdownFile findMarkdownFile(final KnowledgeBundle bundle, final String relativePath) {
        return allDirectories(bundle).stream()
                .flatMap(directory -> directory.markdownFiles().stream())
                .filter(file -> file.relativePath().equals(Path.of(relativePath)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing Markdown file: " + relativePath));
    }

}
