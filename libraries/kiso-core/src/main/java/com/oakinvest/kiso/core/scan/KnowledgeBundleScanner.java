package com.oakinvest.kiso.core.scan;

import com.oakinvest.kiso.core.model.bundle.BundleDirectory;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import com.oakinvest.kiso.core.util.exceptions.KnowledgeBundleScanException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Knowledge bundle scanner - Read a source directory and returns a {@link KnowledgeBundle}.
 */
public class KnowledgeBundleScanner {

    /**
     * Scan a source directory to build a {@link KnowledgeBundle}.
     *
     * @param sourceDirectory source directory
     * @return knowledge bundle
     */
    public KnowledgeBundle scan(final Path sourceDirectory) {
        Path rootDirectory = validateSourceDirectory(sourceDirectory);
        List<BundleDirectory> directories = scanDirectories(rootDirectory);
        List<MarkdownFile> markdownFiles = scanMarkdownFiles(rootDirectory);

        return new KnowledgeBundle(rootDirectory, directories, markdownFiles);
    }

    /**
     * Validate the source directory and returns its absolute path.
     *
     * @param sourceDirectory source directory
     * @return path to the source directory
     */
    private Path validateSourceDirectory(final Path sourceDirectory) {
        if (sourceDirectory == null) {
            throw new KnowledgeBundleScanException("Source directory must not be null.");
        }

        Path rootDirectory = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.exists(rootDirectory)) {
            throw new KnowledgeBundleScanException("Source directory does not exist: " + rootDirectory);
        }
        if (!Files.isDirectory(rootDirectory)) {
            throw new KnowledgeBundleScanException("Source path is not a directory: " + rootDirectory);
        }

        return rootDirectory;
    }

    /**
     * Scan directories and returns all bundle directory.
     *
     * @param rootDirectory directory to scan
     * @return list of bundle directory
     */
    private List<BundleDirectory> scanDirectories(final Path rootDirectory) {
        try (Stream<Path> paths = Files.walk(rootDirectory)) {
            return paths.filter(Files::isDirectory)
                    .map(path -> toBundleDirectory(rootDirectory, path))
                    .sorted(Comparator.comparing(directory -> directory.relativePath().toString()))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to scan directories: " + rootDirectory, exception);
        }
    }

    private BundleDirectory toBundleDirectory(final Path rootDirectory, final Path directory) {
        Path normalizedDirectory = directory.toAbsolutePath().normalize();

        try (Stream<Path> children = Files.list(normalizedDirectory)) {
            List<Path> childDirectories = children.filter(Files::isDirectory)
                    .map(path -> toRelativePath(rootDirectory, path))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();

            try (Stream<Path> files = Files.list(normalizedDirectory)) {
                List<Path> markdownFiles = files.filter(Files::isRegularFile)
                        .filter(this::isMarkdownFile)
                        .map(path -> toRelativePath(rootDirectory, path))
                        .sorted(Comparator.comparing(Path::toString))
                        .toList();

                return new BundleDirectory(
                        normalizedDirectory,
                        toRelativePath(rootDirectory, normalizedDirectory),
                        childDirectories,
                        markdownFiles
                );
            }
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to scan directory: " + normalizedDirectory, exception);
        }
    }

    private List<MarkdownFile> scanMarkdownFiles(final Path rootDirectory) {
        try (Stream<Path> paths = Files.walk(rootDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::isMarkdownFile)
                    .map(path -> toMarkdownFile(rootDirectory, path))
                    .sorted(Comparator.comparing(file -> file.relativePath().toString()))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to scan Markdown files: " + rootDirectory, exception);
        }
    }

    private MarkdownFile toMarkdownFile(final Path rootDirectory, final Path file) {
        Path normalizedFile = file.toAbsolutePath().normalize();

        try {
            return new MarkdownFile(
                    normalizedFile,
                    toRelativePath(rootDirectory, normalizedFile),
                    markdownFileKind(normalizedFile),
                    Files.readString(normalizedFile, StandardCharsets.UTF_8)
            );
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to read Markdown file: " + normalizedFile, exception);
        }
    }

    private boolean isMarkdownFile(final Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".md");
    }

    private MarkdownFileKind markdownFileKind(final Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return switch (fileName) {
            case "index.md" -> MarkdownFileKind.INDEX;
            case "log.md" -> MarkdownFileKind.LOG;
            default -> MarkdownFileKind.CONCEPT;
        };
    }

    private Path toRelativePath(final Path rootDirectory, final Path path) {
        return rootDirectory.relativize(path.toAbsolutePath().normalize());
    }

}
