package com.oakinvest.kiso.core.loader;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.Generated;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.util.FileConstants.CONFIGURATION_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.DESCRIPTION_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.FRONTMATTER_DELIMITER;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.GENERATED_AT_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.GENERATED_BY_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.GENERATED_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.RESOURCE_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TAGS_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TIMESTAMP_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TITLE_KEY;
import static com.oakinvest.kiso.core.util.FrontmatterConstants.TYPE_KEY;
import static com.oakinvest.kiso.core.util.OKFConstants.ROOT_BUNDLE_NAME;

/**
 * Knowledge bundle loader - Load a directory and returns its corresponding {@link KnowledgeBundle}.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class KnowledgeBundleLoader {

    /**
     * Load a directory and returns its corresponding {@link KnowledgeBundle}.
     *
     * @param sourceDirectory source directory
     * @return knowledge bundle
     */
    public static KnowledgeBundle load(final Path sourceDirectory) {
        return load(sourceDirectory, SiteConfiguration.empty());
    }

    /**
     * Load a directory and returns its corresponding {@link KnowledgeBundle} and the website configuration.
     *
     * @param sourceDirectory   source directory
     * @param siteConfiguration site configuration
     * @return knowledge bundle
     */
    public static KnowledgeBundle load(final Path sourceDirectory, final SiteConfiguration siteConfiguration) {
        // We check that the source directory is valid =================================================================
        if (sourceDirectory == null) {
            throw new KnowledgeBundleLoadingException("Source directory is null");
        }

        // Getting the root bundle absolute path.
        // example: /home/straumat/kiso/libraries/kiso-core/target/test-classes/kb-google-example
        Path rootBundleAbsolutePath = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.exists(rootBundleAbsolutePath)) {
            throw new KnowledgeBundleLoadingException("Source directory does not exist: " + rootBundleAbsolutePath);
        }
        if (!Files.isDirectory(rootBundleAbsolutePath)) {
            throw new KnowledgeBundleLoadingException("Source directory is not a directory: " + rootBundleAbsolutePath);
        }

        // We now return the knowledge bundle ==========================================================================
        return KnowledgeBundle.builder()
                .rootBundle(loadBundle(rootBundleAbsolutePath, rootBundleAbsolutePath))
                .siteConfiguration(siteConfiguration)
                .build();
    }

    /**
     * Load bundle.
     *
     * @param rootBundle   root bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return bundle
     */
    private static Bundle loadBundle(final Path rootBundle, final Path bundleToLoad) {
        final Path normalizedPath = bundleToLoad.toAbsolutePath().normalize();

        // Choosing bundle name ========================================================================================
        String bundleName;
        if (Strings.CI.equals(toRelativePath(rootBundle, normalizedPath).toString(), "")) {
            // The root directory has no relative path, so it receives a stable display name.
            bundleName = ROOT_BUNDLE_NAME;
        } else {
            // Child bundles use their full path relative to the root to preserve their hierarchy in the name.
            bundleName = toRelativePath(rootBundle, normalizedPath).toString();
        }

        // Returns the bundle ==========================================================================================
        return Bundle.builder()
                .name(bundleName)
                .absolutePath(normalizedPath)
                .relativePath(toRelativePath(rootBundle, normalizedPath))
                // Bundles.
                .childBundles(loadChildBundles(rootBundle, normalizedPath))
                // Markdown files.
                .markdownFiles(loadMarkdownFiles(rootBundle, normalizedPath))
                .build();
    }

    /**
     * Load child bundles.
     *
     * @param rootBundle   root bundle (absolute path)
     * @param bundleToLoad bundle to load (absolute path)
     * @return bundle list
     */
    private static List<Bundle> loadChildBundles(final Path rootBundle, final Path bundleToLoad) {
        try (Stream<Path> childDirectories = Files.list(bundleToLoad)) {
            return childDirectories
                    // Only directories.
                    .filter(Files::isDirectory)
                    // We don't take the .kiso directory where is the configuration.
                    .filter(path -> !Strings.CI.endsWith(path.toString(), CONFIGURATION_DIRECTORY_NAME))
                    // Order by filename.
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    // Recursive.
                    .map(childDirectory -> loadBundle(rootBundle, childDirectory))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadingException("Unable to load child bundles: " + bundleToLoad, exception);
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
                    // Only files.
                    .filter(Files::isRegularFile)
                    // Only markdown files.
                    .filter(path -> path.getFileName().toString().endsWith(MARKDOWN_EXTENSION))
                    // Order by filename.
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(markdownPath -> loadMarkdownFile(rootBundle, markdownPath))
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadingException("Unable to load Markdown files: " + bundleToLoad, exception);
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
        byte[] bytes = {};

        try {

            // Getting the markdown and the frontmatter ================================================================
            bytes = Files.readAllBytes(normalizedFile);
            final String content = decodeUtf8Strict(bytes);
            final Optional<Frontmatter> frontmatter = loadFrontmatter(content);

            // Return data =============================================================================================
            return MarkdownFile.builder()
                    .fileName(normalizedFile.getFileName().toString())
                    .kind(MarkdownFileKind.from(normalizedFile))
                    .absolutePath(normalizedFile)
                    .relativePath(toRelativePath(rootBundle, normalizedFile))
                    .frontmatter(frontmatter.orElseGet(Frontmatter::empty))
                    .frontmatterPresent(frontmatter.isPresent())
                    .body(removeFrontmatter(content))
                    .build();

        } catch (CharacterCodingException exception) {
            // The file is not a UTF-8 file, but we still return it.
            final String content = decodeUtf8ReplacingInvalidCharacters(bytes);
            return MarkdownFile.builder()
                    .fileName(normalizedFile.getFileName().toString())
                    .kind(MarkdownFileKind.from(normalizedFile))
                    .absolutePath(normalizedFile)
                    .relativePath(toRelativePath(rootBundle, normalizedFile))
                    .frontmatter(Frontmatter.empty())
                    .frontmatterPresent(true)
                    .body(content)
                    .build();
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadingException("Unable to read Markdown file: " + normalizedFile, exception);
        }
    }

    // Frontmatter utils ===============================================================================================

    /**
     * Loads a frontmatter from content.
     *
     * @param content content
     * @return frontmatter
     */
    private static Optional<Frontmatter> loadFrontmatter(final String content) {
        if (findFrontmatterBlock(content) == null) {
            return Optional.empty();
        }
        Map<String, List<String>> data = parseFrontmatter(content);
        return Optional.of(Frontmatter.builder()
                .type(getFrontMatterValue(data, TYPE_KEY))
                .title(getFrontMatterValue(data, TITLE_KEY))
                .description(getFrontMatterValue(data, DESCRIPTION_KEY))
                .resource(getFrontMatterValue(data, RESOURCE_KEY))
                .tags(Objects.requireNonNullElse(data.get(TAGS_KEY), List.of()))
                .timestamp(getFrontMatterValue(data, TIMESTAMP_KEY))
                .generated(loadGenerated(content))
                .extraFields(Objects.requireNonNullElse(getFrontMatterExtraFields(data), new HashMap<>()))
                .build());
    }

    /**
     * Parses the YAML frontmatter subset used by OKF documents.
     *
     * @param content Markdown content
     * @return frontmatter data
     */
    private static Map<String, List<String>> parseFrontmatter(final String content) {
        Map<String, List<String>> data = new LinkedHashMap<>();
        FrontmatterBlock frontmatterBlock = findFrontmatterBlock(content);
        if (frontmatterBlock == null) {
            return data;
        }

        String frontmatterContent = content.substring(
                frontmatterBlock.contentStart(),
                frontmatterBlock.closingDelimiterStart());
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
                if (TAGS_KEY.equals(currentKey) && isInlineList(value)) {
                    values.addAll(parseInlineList(value));
                } else {
                    values.add(cleanFrontMatterValue(value));
                }
            }
            data.put(currentKey, values);
        }

        return data;
    }

    /**
     * Loads the generated metadata from frontmatter.
     *
     * @param content Markdown content
     * @return generated metadata, or null
     */
    private static Generated loadGenerated(final String content) {
        FrontmatterBlock frontmatterBlock = findFrontmatterBlock(content);
        if (frontmatterBlock == null) {
            return null;
        }

        String frontmatterContent = content.substring(
                frontmatterBlock.contentStart(),
                frontmatterBlock.closingDelimiterStart());
        Map<String, String> generatedValues = generatedValues(frontmatterContent);
        if (generatedValues.isEmpty()) {
            return null;
        }

        return Generated.builder()
                .by(generatedValues.get(GENERATED_BY_KEY))
                .at(generatedValues.get(GENERATED_AT_KEY))
                .build();
    }

    /**
     * Extracts generated metadata values from a frontmatter block.
     *
     * @param frontmatterContent frontmatter content
     * @return generated metadata values
     */
    private static Map<String, String> generatedValues(final String frontmatterContent) {
        Map<String, String> generatedValues = new LinkedHashMap<>();
        boolean readingGeneratedBlock = false;
        for (String line : frontmatterContent.split("\\R")) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = StringUtils.trim(line);
            if (readingGeneratedBlock && !Character.isWhitespace(line.charAt(0))) {
                return generatedValues;
            }

            if (readingGeneratedBlock) {
                appendGeneratedValue(generatedValues, trimmedLine);
                continue;
            }

            int separatorIndex = line.indexOf(':');
            if (separatorIndex < 0) {
                continue;
            }

            String key = StringUtils.trim(line.substring(0, separatorIndex));
            if (!GENERATED_KEY.equals(key)) {
                continue;
            }

            String value = StringUtils.trim(line.substring(separatorIndex + 1));
            if (StringUtils.isBlank(value)) {
                readingGeneratedBlock = true;
            } else {
                generatedValues.putAll(parseInlineMap(value));
            }
        }

        return generatedValues;
    }

    /**
     * Appends a generated metadata value.
     *
     * @param generatedValues generated metadata values
     * @param line            frontmatter line
     */
    private static void appendGeneratedValue(final Map<String, String> generatedValues, final String line) {
        int separatorIndex = line.indexOf(':');
        if (separatorIndex < 0) {
            return;
        }

        String key = StringUtils.trim(line.substring(0, separatorIndex));
        String value = StringUtils.trim(line.substring(separatorIndex + 1));
        if (GENERATED_BY_KEY.equals(key) || GENERATED_AT_KEY.equals(key)) {
            generatedValues.put(key, cleanFrontMatterValue(value));
        }
    }

    /**
     * Parses an inline YAML map.
     *
     * @param value inline map value
     * @return map values
     */
    private static Map<String, String> parseInlineMap(final String value) {
        String trimmedValue = StringUtils.trim(value);
        if (!trimmedValue.startsWith("{") || !trimmedValue.endsWith("}")) {
            return Map.of();
        }

        Map<String, String> values = new LinkedHashMap<>();
        String mapContent = trimmedValue.substring(1, trimmedValue.length() - 1);
        for (String entry : mapContent.split(",")) {
            appendGeneratedValue(values, StringUtils.trim(entry));
        }
        return values;
    }

    /**
     * Returns true when a frontmatter value is an inline YAML list.
     *
     * @param value frontmatter value
     * @return {@code true} for an inline list
     */
    private static boolean isInlineList(final String value) {
        String trimmedValue = StringUtils.trim(value);
        return trimmedValue.startsWith("[") && trimmedValue.endsWith("]");
    }

    /**
     * Parses a comma-separated inline YAML list.
     *
     * @param value inline list
     * @return list values
     */
    private static List<String> parseInlineList(final String value) {
        String listContent = StringUtils.trim(value).substring(1, StringUtils.trim(value).length() - 1);
        if (StringUtils.isBlank(listContent)) {
            return List.of();
        }

        return Stream.of(listContent.split(","))
                .map(KnowledgeBundleLoader::cleanFrontMatterValue)
                .toList();
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
        FrontmatterBlock frontmatterBlock = findFrontmatterBlock(content);
        if (frontmatterBlock == null) {
            return content;
        }

        return content.substring(frontmatterBlock.contentEnd());
    }

    /**
     * Finds the frontmatter block boundaries.
     *
     * @param content Markdown content
     * @return frontmatter block boundaries
     */
    private static FrontmatterBlock findFrontmatterBlock(final String content) {
        if (content == null || !content.startsWith(FRONTMATTER_DELIMITER)) {
            return null;
        }

        int firstLineEnd = content.indexOf('\n');
        if (firstLineEnd < 0 || isFrontmatterDelimiter(content.substring(0, firstLineEnd))) {
            return null;
        }

        int closingDelimiterStart = content.indexOf("\n---", firstLineEnd);
        if (closingDelimiterStart < 0) {
            return null;
        }

        int closingDelimiterLineEnd = content.indexOf('\n', closingDelimiterStart + 1);
        if (closingDelimiterLineEnd < 0) {
            String closingDelimiter = content.substring(closingDelimiterStart + 1);
            if (isFrontmatterDelimiter(closingDelimiter)) {
                return null;
            }
            return new FrontmatterBlock(firstLineEnd + 1, closingDelimiterStart, content.length());
        }

        String closingDelimiter = content.substring(closingDelimiterStart + 1, closingDelimiterLineEnd);
        if (isFrontmatterDelimiter(closingDelimiter)) {
            return null;
        }

        return new FrontmatterBlock(firstLineEnd + 1, closingDelimiterStart, closingDelimiterLineEnd + 1);
    }

    /**
     * Returns true when the line is a frontmatter delimiter.
     *
     * @param line line
     * @return {@code true} when the line is a frontmatter delimiter
     */
    private static boolean isFrontmatterDelimiter(final String line) {
        return !FRONTMATTER_DELIMITER.equals(StringUtils.trim(line));
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
     * Returns a relative absolutePath from the rootBundleDirectory directory to the given absolutePath.
     *
     * @param rootDirectory isRoot directory
     * @param path          absolutePath
     * @return relative absolutePath
     */
    private static Path toRelativePath(final Path rootDirectory, final Path path) {
        return rootDirectory.relativize(path.toAbsolutePath().normalize());
    }

    // UTF-8 Utils =====================================================================================================
    private static String decodeUtf8Strict(final byte[] bytes) throws CharacterCodingException {
        CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        return decoder.decode(ByteBuffer.wrap(bytes)).toString();
    }

    private static String decodeUtf8ReplacingInvalidCharacters(final byte[] bytes) {
        CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException exception) {
            throw new IllegalStateException("UTF-8 replacement decoding should not fail", exception);
        }
    }

    /**
     * Frontmatter block boundaries in Markdown content.
     *
     * @param contentStart          content start
     * @param closingDelimiterStart closing delimiter start
     * @param contentEnd            content end
     */
    private record FrontmatterBlock(int contentStart, int closingDelimiterStart, int contentEnd) {
    }

}
