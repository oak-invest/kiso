package com.oakinvest.kiso.core.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import com.oakinvest.kiso.core.model.okf.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationAttester;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationExecutor;
import com.oakinvest.kiso.core.model.okf.markdown.computation.ComputationParameter;
import com.oakinvest.kiso.core.model.okf.markdown.provenance.Source;
import com.oakinvest.kiso.core.model.okf.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.model.okf.markdown.trust.TrustEvent;
import com.oakinvest.kiso.core.util.html.TagNormalizer;
import com.oakinvest.kiso.core.util.types.LifecycleStatus;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.fasterxml.jackson.core.StreamReadFeature.STRICT_DUPLICATE_DETECTION;
import static com.oakinvest.kiso.core.util.contants.FileConstants.CONFIGURATION_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.ATTESTER_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.COMPUTATION_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.DESCRIPTION_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.EXECUTOR_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.EXECUTOR_RECEIPT_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.FRONTMATTER_DELIMITER;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.GENERATED_AT_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.GENERATED_BY_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.GENERATED_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETERS_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETER_NAME_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETER_REQUIRED_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.PARAMETER_TYPE_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.RESOURCE_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.RUNTIME_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCES_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCE_AUTHOR_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCE_ID_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCE_LAST_MODIFIED_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCE_TITLE_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.SOURCE_USAGE_COUNT_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.STALE_AFTER_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.STATUS_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.TAGS_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.TIMESTAMP_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.TITLE_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.TYPE_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.USAGE_WINDOW_FROM_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.USAGE_WINDOW_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.USAGE_WINDOW_TO_KEY;
import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.VERIFIED_KEY;
import static com.oakinvest.kiso.core.util.contants.OKFConstants.ROOT_BUNDLE_NAME;

/**
 * Knowledge bundle loader - Load a directory and returns its corresponding {@link KnowledgeBundle}.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class KnowledgeBundleLoader {

    /** YAML mapper for frontmatter. */
    private static final ObjectMapper YAML_MAPPER = JsonMapper.builder(
                    YAMLFactory.builder()
                            .enable(STRICT_DUPLICATE_DETECTION)
                            .build())
            .build();

    /** Frontmatter extra fields type. */
    private static final TypeReference<LinkedHashMap<String, Object>> EXTRA_FIELDS_TYPE = new TypeReference<>() {
    };

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
        final String frontmatterContent = frontmatterContent(content);
        if (frontmatterContent == null) {
            return Optional.empty();
        }

        // Getting json node from frontmatter content ==================================================================
        final JsonNode frontmatter = readFrontmatter(frontmatterContent);

        // Treating tags ===============================================================================================
        final List<String> tags = textList(frontmatter.get(TAGS_KEY));
        final List<String> tagSlugs = tags.stream().map(TagNormalizer::normalize).toList();

        // Returning frontmatter =======================================================================================
        return Optional.of(Frontmatter.builder()
                .type(textValue(frontmatter, TYPE_KEY))
                .title(textValue(frontmatter, TITLE_KEY))
                .description(textValue(frontmatter, DESCRIPTION_KEY))
                .resource(textValue(frontmatter, RESOURCE_KEY))
                .tags(tags)
                .tagSlugs(tagSlugs)
                .timestamp(textValue(frontmatter, TIMESTAMP_KEY))
                .sources(sources(frontmatter.get(SOURCES_KEY)))
                .usageWindow(usageWindow(frontmatter.get(USAGE_WINDOW_KEY)))
                .generated(generated(frontmatter.get(GENERATED_KEY)))
                .verified(verifications(frontmatter.get(VERIFIED_KEY)))
                .status(LifecycleStatus.from(textValue(frontmatter, STATUS_KEY)))
                .staleAfter(textValue(frontmatter, STALE_AFTER_KEY))
                .runtime(textValue(frontmatter, RUNTIME_KEY))
                .parameters(parameters(frontmatter.get(PARAMETERS_KEY)))
                .computation(textValue(frontmatter, COMPUTATION_KEY))
                .executor(executor(frontmatter.get(EXECUTOR_KEY)))
                .attester(attester(frontmatter.get(ATTESTER_KEY)))
                .extraFields(extraFields(frontmatter))
                .build());
    }

    /**
     * Reads the YAML frontmatter content.
     *
     * @param frontmatterContent frontmatter content
     * @return frontmatter tree
     */
    private static JsonNode readFrontmatter(final String frontmatterContent) {
        try {
            final JsonNode frontmatter = YAML_MAPPER.readTree(frontmatterContent);
            if (frontmatter == null || frontmatter.isMissingNode() || !frontmatter.isObject()) {
                return YAML_MAPPER.createObjectNode();
            }
            return frontmatter;
        } catch (IOException exception) {
            throw new KnowledgeBundleLoadingException("Unable to parse frontmatter: " + exception.getMessage(), exception);
        }
    }

    /**
     * Returns the frontmatter content.
     *
     * @param content Markdown content
     * @return frontmatter content, or null
     */
    private static String frontmatterContent(final String content) {
        final FrontmatterBlock frontmatterBlock = findFrontmatterBlock(content);
        if (frontmatterBlock == null) {
            return null;
        }

        return content.substring(
                frontmatterBlock.contentStart(),
                frontmatterBlock.closingDelimiterStart());
    }

    /**
     * Returns source metadata.
     *
     * @param sources sources node
     * @return source metadata
     */
    private static List<Source> sources(final JsonNode sources) {
        if (sources == null || !sources.isArray()) {
            return List.of();
        }

        final List<Source> values = new ArrayList<>();
        sources.forEach(source -> values.add(source(source)));
        return values;
    }

    /**
     * Creates source metadata.
     *
     * @param source source node
     * @return source metadata
     */
    private static Source source(final JsonNode source) {
        return Source.builder()
                .id(textValue(source, SOURCE_ID_KEY))
                .resource(textValue(source, RESOURCE_KEY))
                .title(textValue(source, SOURCE_TITLE_KEY))
                .author(actor(source, SOURCE_AUTHOR_KEY))
                .usageCount(longValue(source, SOURCE_USAGE_COUNT_KEY))
                .lastModified(textValue(source, SOURCE_LAST_MODIFIED_KEY))
                .usageWindow(usageWindow(source.get(USAGE_WINDOW_KEY)))
                .build();
    }

    /**
     * Returns generated metadata from frontmatter.
     *
     * @param generated generated node
     * @return generated metadata, or null
     */
    private static TrustEvent generated(final JsonNode generated) {
        if (generated == null || !generated.isObject()) {
            return null;
        }

        return TrustEvent.builder()
                .by(actor(generated, GENERATED_BY_KEY))
                .at(textValue(generated, GENERATED_AT_KEY))
                .build();
    }

    /**
     * Returns verification metadata from frontmatter.
     *
     * @param verified verified node
     * @return verification metadata
     */
    private static List<TrustEvent> verifications(final JsonNode verified) {
        if (verified == null || verified.isNull() || verified.isMissingNode()) {
            return List.of();
        }
        if (verified.isObject()) {
            return List.of(verification(verified));
        }
        if (!verified.isArray()) {
            return List.of();
        }

        final List<TrustEvent> values = new ArrayList<>();
        verified.forEach(verification -> values.add(verification(verification)));
        return values;
    }

    /**
     * Creates verification metadata.
     *
     * @param verification verification node
     * @return verification metadata
     */
    private static TrustEvent verification(final JsonNode verification) {
        return TrustEvent.builder()
                .by(actor(verification, GENERATED_BY_KEY))
                .at(textValue(verification, GENERATED_AT_KEY))
                .build();
    }

    /**
     * Returns an actor value.
     *
     * @param node      node
     * @param fieldName field name
     * @return actor, or null
     */
    private static Actor actor(final JsonNode node, final String fieldName) {
        return Actor.of(textValue(node, fieldName));
    }

    /**
     * Creates usage window metadata.
     *
     * @param usageWindow usage window node
     * @return usage window metadata, or null
     */
    private static UsageWindow usageWindow(final JsonNode usageWindow) {
        if (usageWindow == null || !usageWindow.isObject()) {
            return null;
        }

        return UsageWindow.builder()
                .from(textValue(usageWindow, USAGE_WINDOW_FROM_KEY))
                .to(textValue(usageWindow, USAGE_WINDOW_TO_KEY))
                .build();
    }

    /**
     * Returns computation parameters from frontmatter.
     *
     * @param parameters parameters node
     * @return computation parameters
     */
    private static List<ComputationParameter> parameters(final JsonNode parameters) {
        if (parameters == null || !parameters.isArray()) {
            return List.of();
        }

        final List<ComputationParameter> values = new ArrayList<>();
        parameters.forEach(parameter -> values.add(parameter(parameter)));
        return values;
    }

    /**
     * Creates computation parameter metadata.
     *
     * @param parameter parameter node
     * @return computation parameter metadata
     */
    private static ComputationParameter parameter(final JsonNode parameter) {
        return ComputationParameter.builder()
                .name(textValue(parameter, PARAMETER_NAME_KEY))
                .type(textValue(parameter, PARAMETER_TYPE_KEY))
                .required(booleanValue(parameter, PARAMETER_REQUIRED_KEY))
                .build();
    }

    /**
     * Returns executor metadata from frontmatter.
     *
     * @param executor executor node
     * @return executor metadata, or null
     */
    private static ComputationExecutor executor(final JsonNode executor) {
        if (executor == null || !executor.isObject()) {
            return null;
        }

        return ComputationExecutor.builder()
                .resource(textValue(executor, RESOURCE_KEY))
                .receipt(textList(executor.get(EXECUTOR_RECEIPT_KEY)))
                .build();
    }

    /**
     * Returns attester metadata from frontmatter.
     *
     * @param attester attester node
     * @return attester metadata, or null
     */
    private static ComputationAttester attester(final JsonNode attester) {
        if (attester == null || !attester.isObject()) {
            return null;
        }

        return ComputationAttester.builder()
                .resource(textValue(attester, RESOURCE_KEY))
                .build();
    }

    /**
     * Returns a text value.
     *
     * @param node      node
     * @param fieldName field name
     * @return text value, or null
     */
    private static String textValue(final JsonNode node, final String fieldName) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return null;
        }

        return node.get(fieldName).asText();
    }

    /**
     * Returns text values.
     *
     * @param node node
     * @return text values
     */
    private static List<String> textList(final JsonNode node) {
        if (node == null || node.isNull()) {
            return List.of();
        }

        if (!node.isArray()) {
            return List.of(node.asText());
        }

        final List<String> values = new ArrayList<>();
        node.forEach(value -> values.add(value.asText()));
        return values;
    }

    /**
     * Returns a long value.
     *
     * @param node      node
     * @param fieldName field name
     * @return long value, or null
     */
    private static Long longValue(final JsonNode node, final String fieldName) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return null;
        }

        final JsonNode value = node.get(fieldName);
        if (value.isIntegralNumber()) {
            return value.asLong();
        }
        if (StringUtils.isBlank(value.asText())) {
            return null;
        }
        try {
            return Long.parseLong(value.asText());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Returns a boolean value.
     *
     * @param node      node
     * @param fieldName field name
     * @return boolean value, or null
     */
    private static Boolean booleanValue(final JsonNode node, final String fieldName) {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            return null;
        }

        final JsonNode value = node.get(fieldName);
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (StringUtils.isBlank(value.asText())) {
            return null;
        }
        return Boolean.parseBoolean(value.asText());
    }

    /**
     * Returns producer-defined fields.
     *
     * @param frontmatter frontmatter node
     * @return producer-defined fields
     */
    private static Map<String, Object> extraFields(final JsonNode frontmatter) {
        return YAML_MAPPER.convertValue(frontmatter, EXTRA_FIELDS_TYPE);
    }

    /**
     * Removes the frontmatter block from Markdown content.
     *
     * @param content Markdown content
     * @return Markdown content without frontmatter
     */
    private static String removeFrontmatter(final String content) {
        final FrontmatterBlock frontmatterBlock = findFrontmatterBlock(content);
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

        final int firstLineEnd = content.indexOf('\n');
        if (firstLineEnd < 0 || isFrontmatterDelimiter(content.substring(0, firstLineEnd))) {
            return null;
        }

        final int closingDelimiterStart = content.indexOf("\n---", firstLineEnd);
        if (closingDelimiterStart < 0) {
            return null;
        }

        final int closingDelimiterLineEnd = content.indexOf('\n', closingDelimiterStart + 1);
        if (closingDelimiterLineEnd < 0) {
            final String closingDelimiter = content.substring(closingDelimiterStart + 1);
            if (isFrontmatterDelimiter(closingDelimiter)) {
                return null;
            }
            return new FrontmatterBlock(firstLineEnd + 1, closingDelimiterStart, content.length());
        }

        final String closingDelimiter = content.substring(closingDelimiterStart + 1, closingDelimiterLineEnd);
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
        final CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        return decoder.decode(ByteBuffer.wrap(bytes)).toString();
    }

    private static String decodeUtf8ReplacingInvalidCharacters(final byte[] bytes) {
        final CharsetDecoder decoder = StandardCharsets.UTF_8
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
