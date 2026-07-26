package com.oakinvest.kiso.core.model.okf.markdown;

import lombok.Builder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.FileExtensionsConstants.HTML_EXTENSION;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param fileName           file name
 * @param kind               file kind
 * @param absolutePath       absolute path (with the file name)
 * @param relativePath       relative path to the root bundle (with the file name)
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
     * Returns the concept id.
     * The path of the concept's file within the bundle, with the .md suffix removed.
     * For example, tables/users.md has a concept ID tables/users.
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
            // Concept file ============================================================================================
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
        return frontmatter.parsedTimestamp();
    }

    /**
     * Returns the HTML file name corresponding to the Markdown file.
     *
     * @return HTML file name
     */
    public String htmlFilename() {
        return FilenameUtils.removeExtension(fileName) + HTML_EXTENSION;
    }

    /**
     * Returns the HTML path corresponding to the Markdown file (with the file name).
     *
     * @return HTML file path
     */
    public String htmlFilePath() {
        String markdownFilePath = FilenameUtils.separatorsToUnix(relativePath.toString());
        return FilenameUtils.removeExtension(markdownFilePath) + HTML_EXTENSION;
    }

}
