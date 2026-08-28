package com.oakinvest.kiso.core.model.markdown;

import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
import lombok.Builder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.HTML_EXTENSION;
import static com.oakinvest.kiso.core.util.contants.OKFConstants.ROOT_BUNDLE_NAME;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * Markdown file discovered inside a knowledge bundle.
 * <p>
 * Example for index.md :
 * - markdownFile.fileName = index.md
 * - markdownFile.absolutePath = /home/.../kb-google-example-v0.1/index.md
 * - markdownFile.relativePath = index.md
 * - markdownFile.conceptId = null
 * - markdownFile.title = index.md
 * - markdownFile.htmlFilename = index.html
 * - markdownFile.htmlFilePath = index.html
 * - markdownFile.bundlePath =
 * - markdownFile.bundleName = index
 * <p>
 * Example for tables/events_.md:
 * - markdownFile.fileName = events_.md
 * - markdownFile.absolutePath = /home/.../kb-google-example-v0.1/tables/events_.md
 * - markdownFile.relativePath = tables/events_.md
 * - markdownFile.conceptId = tables/events_
 * - markdownFile.title = Events table (Google Analytics BigQuery Export)
 * - markdownFile.htmlFilename = events_.html
 * - markdownFile.htmlFilePath = tables/events_.html
 * - markdownFile.bundlePath = tables
 * - markdownFile.bundleName = tables
 *
 * @param fileName           file name (example: "index.md" for index.md in the root bundle or "events_.md" for "tables/events_.md")
 * @param kind               file kind
 * @param absolutePath       absolute path (example: "/home/.../kb-google-example-v0.1/index.md" for index in a root bundle or "/home/.../kb-google-example-v0.1/tables/events_.md" for "tables/events_.md")
 * @param relativePath       relative path to the root bundle (example "index.md" for index.md in the root bundle or "tables/events_.md" for "tables/events_.md")
 * @param frontmatter        frontmatter metadata
 * @param frontmatterPresent whether a frontmatter block exists in the source file
 * @param body               original Markdown content without frontmatter
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        String fileName,
        MarkdownFileKind kind,
        Path absolutePath,
        Path relativePath,
        Frontmatter frontmatter,
        boolean frontmatterPresent,
        @Nullable String body
) {

    /**
     * Creates a Markdown file with safe default values.
     */
    public MarkdownFile {
        frontmatter = Objects.requireNonNullElse(frontmatter, Frontmatter.empty());
    }

    /**
     * Returns the bundle path.
     * For "index.md" in the root bundle:
     * For "user_count.md" in references/metrics/user_count.md: references/metrics
     *
     * @return bundle path, or an empty string for a root Markdown file
     */
    public String bundlePath() {
        String unixRelativePath = FilenameUtils.separatorsToUnix(relativePath.toString());
        return FilenameUtils.getPathNoEndSeparator(unixRelativePath);

    }

    /**
     * Returns the bundle name.
     * For "index.md" in the root bundle: index
     * For "user_count.md" in references/metrics/user_count.md: metrics
     *
     * @return bundle name, or an empty string for a root Markdown file
     */
    public String bundleName() {
        final String bundleName = FilenameUtils.getName(bundlePath());
        if (StringUtils.isBlank(bundleName)) {
            return ROOT_BUNDLE_NAME;
        }
        return bundleName;
    }

    /**
     * Returns the concept id.
     * The path of the concept's file within the bundle, with the .md suffix removed.
     * For "index.md" in the root bundle: returns null
     * For "events.md" in tables/events_.md: returns "tables/events_"
     *
     * @return concept id
     */
    public @Nullable String conceptId() {
        if (kind.equals(CONCEPT)) {
            String filePath = FilenameUtils.separatorsToUnix(relativePath.toString());
            return FilenameUtils.removeExtension(filePath);
        } else {
            return null;
        }
    }

    /**
     * Returns the page title.
     * For "index.md" in the root bundle: index.md
     * For "events.md" in tables/events_.md: Events table (Google Analytics BigQuery Export)
     *
     * @return page title
     */
    public String title() {
        if (StringUtils.isNotBlank(frontmatter.title())) {
            return frontmatter.title();
        }
        return fileName;
    }

    /**
     * Returns the page description.
     *
     * @return page description
     */
    public String description() {
        if (kind().equals(INDEX)) {
            // Index file ==============================================================================================
            if (Strings.CI.equals(relativePath.toString(), INDEX.getFileName())) {
                return "Knowledge bundle index";
            } else {
                // Returns example: "/datasets/index.md" -> "Index of /datasets"
                return "Index of " + Strings.CI.replace(relativePath().toString(), "/" + INDEX.getFileName(), "");
            }
        } else {
            // Concept & log files =====================================================================================
            if (StringUtils.isNotBlank(frontmatter.description())) {
                return frontmatter.description();
            } else {
                return relativePath().toString();
            }
        }
    }

    /**
     * Returns the frontmatter timestamp.
     *
     * @return timestamp
     */
    public @Nullable OffsetDateTime timestamp() {
        if (frontmatter.generated() != null && frontmatter.generated().parsedAt() != null) {
            return frontmatter.generated().parsedAt();
        }
        // V0.1 compatibility: use the timestamp field if the generated field is not present.
        return frontmatter.parsedTimestamp();
    }

    /**
     * Returns the HTML file name corresponding to the Markdown file.
     * For "index.md" in the root bundle: index.html
     * For "events.md" in tables/events_.md: events.html
     *
     * @return HTML file name
     */
    public String htmlFilename() {
        return FilenameUtils.removeExtension(fileName) + HTML_EXTENSION;
    }

    /**
     * Returns the HTML path corresponding to the Markdown file (with the file name).
     * For "index.md" in the root bundle: index.html
     * For "events.md" in tables/events_.md: tables/events.html
     *
     * @return HTML file path
     */
    public String htmlFilePath() {
        String markdownFilePath = FilenameUtils.separatorsToUnix(relativePath.toString());
        return FilenameUtils.removeExtension(markdownFilePath) + HTML_EXTENSION;
    }

}
