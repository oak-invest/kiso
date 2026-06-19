package com.oakinvest.kiso.core.loader;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.exceptions.KnowledgeBundleScanException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.model.markdown.Frontmatter.DESCRIPTION_KEY;
import static com.oakinvest.kiso.core.model.markdown.Frontmatter.RESOURCE_KEY;
import static com.oakinvest.kiso.core.model.markdown.Frontmatter.TAGS_KEY;
import static com.oakinvest.kiso.core.model.markdown.Frontmatter.TIMESTAMP_KEY;
import static com.oakinvest.kiso.core.model.markdown.Frontmatter.TITLE_KEY;
import static com.oakinvest.kiso.core.model.markdown.Frontmatter.TYPE_KEY;
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
    public KnowledgeBundle load(final Path sourceDirectory) {
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
            // Exploring the markdown ======================================================================================
            final String content = Files.readString(normalizedFile, UTF_8);
            Map<String, List<String>> frontMatterData = parseFrontmatter(content);
            Frontmatter frontmatter = Frontmatter.builder()
                    .type(getFrontMatterValue(frontMatterData, TYPE_KEY))
                    .title(getFrontMatterValue(frontMatterData, TITLE_KEY))
                    .description(getFrontMatterValue(frontMatterData, DESCRIPTION_KEY))
                    .resource(getFrontMatterValue(frontMatterData, RESOURCE_KEY))
                    .tags(frontMatterData.get(TAGS_KEY))
                    .timestamp(getFrontMatterOffsetDateTimeValue(frontMatterData, TIMESTAMP_KEY))
                    .extraFields(getFrontMatterExtraFields(frontMatterData))
                    .build();

            // Return data =================================================================================================
            return MarkdownFile.builder()
                    .path(normalizedFile)
                    .relativePath(toRelativePath(rootDirectory, normalizedFile))
                    .frontmatter(frontmatter)
                    .content(removeFrontmatter(content))
                    .build();
        } catch (IOException exception) {
            throw new KnowledgeBundleScanException("Unable to read Markdown file: " + normalizedFile, exception);
        }
    }

    /**
     * Parses the YAML frontmatter subset used by OKF documents.
     *
     * @param content Markdown content
     * @return frontmatter data
     */
    private Map<String, List<String>> parseFrontmatter(final String content) {
        Map<String, List<String>> data = new LinkedHashMap<>();
        if (content == null || !content.startsWith("---")) {
            return data;
        }

        int firstLineEnd = content.indexOf('\n');
        if (firstLineEnd < 0 || !isFrontmatterDelimiter(content.substring(0, firstLineEnd))) {
            return data;
        }

        int closingDelimiterStart = content.indexOf("\n---", firstLineEnd);
        if (closingDelimiterStart < 0) {
            return data;
        }

        String frontmatterContent = content.substring(firstLineEnd + 1, closingDelimiterStart);
        String currentKey = null;
        for (String line : frontmatterContent.split("\\R")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = StringUtils.trim(line);
            if (trimmedLine.startsWith("- ") && currentKey != null) {
                data.get(currentKey).add(cleanFrontMatterValue(trimmedLine.substring(2)));
                continue;
            }

            if (Character.isWhitespace(line.charAt(0)) && currentKey != null && !data.get(currentKey).isEmpty()) {
                List<String> values = data.get(currentKey);
                values.set(0, values.getFirst() + " " + cleanFrontMatterValue(trimmedLine));
                continue;
            }

            int separatorIndex = line.indexOf(':');
            if (separatorIndex < 0) {
                continue;
            }

            currentKey = StringUtils.trim(line.substring(0, separatorIndex));
            String value = StringUtils.trim(line.substring(separatorIndex + 1));
            List<String> values = new java.util.ArrayList<>();
            if (StringUtils.isNotBlank(value)) {
                values.add(cleanFrontMatterValue(value));
            }
            data.put(currentKey, values);
        }

        return data;
    }

    /**
     * Cleans a frontmatter value.
     *
     * @param value value
     * @return cleaned value
     */
    private String cleanFrontMatterValue(final String value) {
        String trimmedValue = StringUtils.trim(value);
        if (StringUtils.length(trimmedValue) >= 2
                && ((trimmedValue.startsWith("'") && trimmedValue.endsWith("'"))
                || (trimmedValue.startsWith("\"") && trimmedValue.endsWith("\"")))) {
            return trimmedValue.substring(1, trimmedValue.length() - 1);
        }
        return trimmedValue;
    }

    /**
     * Removes the frontmatter block from Markdown content.
     *
     * @param content Markdown content
     * @return Markdown content without frontmatter
     */
    private String removeFrontmatter(final String content) {
        if (content == null || !content.startsWith("---")) {
            return content;
        }

        int firstLineEnd = content.indexOf('\n');
        if (firstLineEnd < 0 || !isFrontmatterDelimiter(content.substring(0, firstLineEnd))) {
            return content;
        }

        int closingDelimiterStart = content.indexOf("\n---", firstLineEnd);
        if (closingDelimiterStart < 0) {
            return content;
        }

        int closingDelimiterLineEnd = content.indexOf('\n', closingDelimiterStart + 1);
        if (closingDelimiterLineEnd < 0) {
            return "";
        }

        String closingDelimiter = content.substring(closingDelimiterStart + 1, closingDelimiterLineEnd);
        if (!isFrontmatterDelimiter(closingDelimiter)) {
            return content;
        }

        return content.substring(closingDelimiterLineEnd + 1);
    }

    /**
     * Returns true when the line is a frontmatter delimiter.
     *
     * @param line line
     * @return true when the line is a frontmatter delimiter
     */
    private boolean isFrontmatterDelimiter(final String line) {
        return "---".equals(StringUtils.trim(line));
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

    /**
     * Returns front matter single value.
     *
     * @param data data
     * @param key  key
     * @return value
     */
    private String getFrontMatterValue(final Map<String, List<String>> data, final String key) {
        List<String> values = data.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        } else {
            return StringUtils.normalizeSpace(values.getFirst());
        }
    }

    /**
     * Returns front matter extra fields.
     *
     * @param data front matter data
     * @return extra fields
     */
    private Map<String, Object> getFrontMatterExtraFields(final Map<String, List<String>> data) {
        return new LinkedHashMap<>(data);
    }

    /**
     * Returns front matter date time value.
     *
     * @param data front matter data
     * @param key  key
     * @return date time value
     */
    private OffsetDateTime getFrontMatterOffsetDateTimeValue(final Map<String, List<String>> data, final String key) {
        String value = getFrontMatterValue(data, key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new KnowledgeBundleScanException("Invalid ISO 8601 date time for frontmatter key '" + key + "': " + value, exception);
        }
    }

}
