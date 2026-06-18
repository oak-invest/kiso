package com.oakinvest.kiso.core.loader;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.exceptions.KnowledgeBundleScanException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Knowledge bundle loader - Load a directory and returns its corresponding {@link KnowledgeBundle}.
 */
public class KnowledgeBundleLoader {

    /**
     * Load a directory to build its corresponding {@link KnowledgeBundle}.
     *
     * @param sourceDirectory content directory
     * @return knowledge bundle
     */
    public KnowledgeBundle loadKnowledgeBundle(final Path sourceDirectory) {
        // We check that the source directory is valid =================================================================
        if (sourceDirectory == null) {
            throw new KnowledgeBundleScanException("Source directory is null");
        }

        // Getting the complete path.
        // example: /home/straumat/kiso/libraries/kiso-core/target/test-classes/kb-google-example
        Path rootDirectory = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.exists(rootDirectory)) {
            throw new KnowledgeBundleScanException("Source directory does not exist: " + rootDirectory);
        }
        if (!Files.isDirectory(rootDirectory)) {
            throw new KnowledgeBundleScanException("Source path is not a directory: " + rootDirectory);
        }

        // We now return the knowledge bundle ==========================================================================
        return KnowledgeBundle.builder()
                .rootBundlePath(rootDirectory)
                .rootBundle(loadBundleDirectory(rootDirectory, rootDirectory))
                .build();
    }

    /**
     * Load a directory to build its corresponding {@link Bundle}.
     *
     * @param rootDirectory root directory
     * @param directory     directory
     * @return bundle directory
     */
    private Bundle loadBundleDirectory(final Path rootDirectory, final Path directory) {
        Path normalizedDirectory = directory.toAbsolutePath().normalize();

        return Bundle.builder()
                .path(normalizedDirectory)
                .relativePath(toRelativePath(rootDirectory, normalizedDirectory))
                // Directories.
                .childBundleDirectories(loadChildDirectories(rootDirectory, normalizedDirectory))
                // Files.
                .markdownFiles(loadDirectMarkdownFiles(rootDirectory, normalizedDirectory))
                .build();
    }

    /**
     * Load child directories of a directory.
     *
     * @param rootDirectory root directory
     * @param directory     directory
     * @return bundle list
     */
    private List<Bundle> loadChildDirectories(final Path rootDirectory, final Path directory) {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(Files::isDirectory)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    // Here is the recursive part.
                    .map(path -> loadBundleDirectory(rootDirectory, path))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to load child directories: " + directory, exception);
        }
    }

    /**
     * Load direct Markdown files of a directory.
     *
     * @param rootDirectory root directory
     * @param directory     directory
     * @return files
     */
    private List<MarkdownFile> loadDirectMarkdownFiles(final Path rootDirectory, final Path directory) {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> StringUtils.endsWithIgnoreCase(path.getFileName().toString(), ".md"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(path -> getMarkdownFile(rootDirectory, path))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to load Markdown files: " + directory, exception);
        }
    }

    /**
     * Returns a markdown file from a file.
     *
     * @param rootDirectory root directory
     * @param file          file
     * @return file
     */
    private MarkdownFile getMarkdownFile(final Path rootDirectory, final Path file) {
        Path normalizedFile = file.toAbsolutePath().normalize();
        try {
            return MarkdownFile.builder()
                    .path(normalizedFile)
                    .relativePath(toRelativePath(rootDirectory, normalizedFile))
                    .content(Files.readString(normalizedFile, UTF_8))
                    .build();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to read Markdown file: " + normalizedFile, exception);
        }
    }

    /**
     * Returns a relative path from the rootBundleDirectory directory to the given path.
     *
     * @param rootDirectory root directory
     * @param path          path
     * @return relative path
     */
    private Path toRelativePath(final Path rootDirectory, final Path path) {
        return rootDirectory.relativize(path.toAbsolutePath().normalize());
    }

}
