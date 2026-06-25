package com.oakinvest.kiso.core.loading;

import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadException;
import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.util.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.DESCRIPTION_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.FRONTMATTER_DELIMITER;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.RESOURCE_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TAGS_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TIMESTAMP_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TITLE_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TYPE_KEY;
import static com.oakinvest.kiso.core.util.OKFConstants.ROOT_BUNDLE_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Knowledge bundle loader - Load a directory and returns its corresponding {@link KnowledgeBundle}.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class KnowledgeBundleLoader {

    /**
     * Load a directory to build its corresponding {@link KnowledgeBundle}.
     *
     * @param sourceDirectory content directory
     * @return knowledge bundle
     */
    public static KnowledgeBundle load(final Path sourceDirectory) {
        // We check that the source directory is valid =================================================================
        if (sourceDirectory == null) {
            throw new KnowledgeBundleLoadException("Source directory is null");
        }

        // Getting the isRoot bundle absolute path.
        // example: /home/straumat/kiso/libraries/kiso-core/target/test-classes/kb-google-example
        Path rootBundleAbsolutePath = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.exists(rootBundleAbsolutePath)) {
            throw new KnowledgeBundleLoadException("Source directory does not exist: " + rootBundleAbsolutePath);
        }
        if (!Files.isDirectory(rootBundleAbsolutePath)) {
            throw new KnowledgeBundleLoadException("Source directory is not a directory: " + rootBundleAbsolutePath);
        }

        // We now return the knowledge bundle ==========================================================================
        return KnowledgeBundle.builder()
                .rootBundle(loadBundle(rootBundleAbsolutePath, rootBundleAbsolutePath))
                .build();
    }

    /**
     * Load bundle.
     *
     * @param rootBundle   isRoot bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return bundle
     */
    private static Bundle loadBundle(final Path rootBundle, final Path bundleToLoad) {
        final Path normalizedPath = bundleToLoad.toAbsolutePath().normalize();

        // Choosing bundle name.
        String bundleName;
        if (Strings.CI.equals(toRelativePath(rootBundle, normalizedPath).toString(), "")) {
            bundleName = ROOT_BUNDLE_NAME;
        } else {
            bundleName = toRelativePath(rootBundle, normalizedPath).toString();
        }

        return Bundle.builder()
                .name(bundleName)
                .absolutePath(normalizedPath)
                .relativePath(toRelativePath(rootBundle, normalizedPath))
                // Directories.
                .childBundles(loadChildBundles(rootBundle, normalizedPath))
                // Files.
                .markdownFiles(loadMarkdownFiles(rootBundle, normalizedPath))
                .build();
    }

    /**
     * Load child bundles.
     *
     * @param rootBundle   isRoot bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return bundle list
     */
    private static List<Bundle> loadChildBundles(final Path rootBundle, final Path bundleToLoad) {
        try (Stream<Path> childDirectories = Files.list(bundleToLoad)) {
            return childDirectories
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    // Recursive.
                    .map(childDirectory -> loadBundle(rootBundle, childDirectory))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadException("Unable to load child directories: " + bundleToLoad, exception);
        }
    }

    /**
     * Load direct Markdown files of a directory.
     *
     * @param rootBundle   isRoot bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return Markdown files
     */
    private static List<MarkdownFile> loadMarkdownFiles(final Path rootBundle, final Path bundleToLoad) {
        try (Stream<Path> bundleEntries = Files.list(bundleToLoad)) {
            return bundleEntries
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(MARKDOWN_EXTENSION))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(markdownPath -> loadMarkdownFile(rootBundle, markdownPath))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadException("Unable to load Markdown files: " + bundleToLoad, exception);
        }
    }

    /**
     * Returns a markdown file from a file.
     *
     * @param rootBundle   isRoot bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return Markdown file
     */
    private static MarkdownFile loadMarkdownFile(final Path rootBundle, final Path bundleToLoad) {
        final Path normalizedFile = bundleToLoad.toAbsolutePath().normalize();

        try {
            // Exploring the markdown ==================================================================================
            final String content = Files.readString(normalizedFile, UTF_8);
            Map<String, List<String>> frontMatterData = parseFrontmatter(content);
            Frontmatter frontmatter = Frontmatter.builder()
                    .type(getFrontMatterValue(frontMatterData, TYPE_KEY))
                    .title(getFrontMatterValue(frontMatterData, TITLE_KEY))
                    .description(getFrontMatterValue(frontMatterData, DESCRIPTION_KEY))
                    .resource(getFrontMatterValue(frontMatterData, RESOURCE_KEY))
                    .tags(ObjectUtils.getIfNull(frontMatterData.get(TAGS_KEY), List.of()))
                    .timestamp(getFrontMatterOffsetDateTimeValue(frontMatterData, TIMESTAMP_KEY))
                    .extraFields(ObjectUtils.getIfNull(getFrontMatterExtraFields(frontMatterData), new HashMap<>()))
                    .build();

            // Return data =============================================================================================
            return MarkdownFile.builder()
                    .fileName(normalizedFile.getFileName().toString())
                    .kind(MarkdownFileKind.from(normalizedFile))
                    .absolutePath(normalizedFile)
                    .relativePath(toRelativePath(rootBundle, normalizedFile))
                    .frontmatter(frontmatter)
                    .content(removeFrontmatter(content))
                    .build();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadException("Unable to read Markdown file: " + normalizedFile, exception);
        }
    }

    // Frontmatter utils ===============================================================================================

    /**
     * Parses the YAML frontmatter subset used by OKF documents.
     *
     * @param content Markdown content
     * @return frontmatter data
     */
    private static Map<String, List<String>> parseFrontmatter(final String content) {
        Map<String, List<String>> data = new LinkedHashMap<>();
        if (content == null || !content.startsWith(FRONTMATTER_DELIMITER)) {
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
    private static String cleanFrontMatterValue(final String value) {
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
    private static String removeFrontmatter(final String content) {
        if (content == null || !content.startsWith(FRONTMATTER_DELIMITER)) {
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
    private static boolean isFrontmatterDelimiter(final String line) {
        return FRONTMATTER_DELIMITER.equals(StringUtils.trim(line));
    }

    /**
     * Returns front matter single value.
     *
     * @param data data
     * @param key  key
     * @return value
     */
    private static String getFrontMatterValue(final Map<String, List<String>> data, final String key) {
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
    private static Map<String, Object> getFrontMatterExtraFields(final Map<String, List<String>> data) {
        return new LinkedHashMap<>(data);
    }

    /**
     * Returns front matter date time value.
     *
     * @param data front matter data
     * @param key  key
     * @return date time value
     */
    private static OffsetDateTime getFrontMatterOffsetDateTimeValue(final Map<String, List<String>> data, final String key) {
        String value = getFrontMatterValue(data, key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new KnowledgeBundleLoadException("Invalid ISO 8601 date time for frontmatter key '" + key + "': " + value, exception);
        }
    }

    /**
     * Returns a relative absolutePath from the rootBundleDirectory directory to the given absolutePath.
     *
     * @param rootDirectory isRoot directory
     * @param path          absolutePath
     * @return relative absolutePath
     */
    private static Path toRelativePath(final Path rootDirectory, final Path path) {
        return rootDirectory.relativize(path.toAbsolutePath().normalize());
    }

}
