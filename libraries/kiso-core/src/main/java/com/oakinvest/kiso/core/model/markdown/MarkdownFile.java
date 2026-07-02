package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.time.OffsetDateTime;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.FileExtensionsConstants.HTML_EXTENSION;
import static com.oakinvest.kiso.core.util.FileExtensionsConstants.MARKDOWN_EXTENSION;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param fileName     file name
 * @param kind         file kind
 * @param absolutePath absolute path (with the file name)
 * @param relativePath relative path to the root bundle (with the file name)
 * @param frontmatter  frontmatter metadata
 * @param body         original Markdown content without frontmatter
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        String fileName,
        MarkdownFileKind kind,
        Path absolutePath,
        Path relativePath,
        @Nullable Frontmatter frontmatter,
        String body
) {

    /**
     * Returns true if the frontmatter exists in the Markdown file.
     *
     * @return true if a frontmatter exists
     */
    public boolean hasFrontmatter() {
        return frontmatter != null;
    }

    /**
     * Returns the page title.
     *
     * @return page title
     */
    public String title() {
        if (hasFrontmatter() && StringUtils.isNotBlank(frontmatter.title())) {
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
                return "Index of " + Strings.CI.replace(relativePath().toString(), "/" + fileName, "");
            }
        } else {
            // Concept file ============================================================================================
            if (hasFrontmatter() && StringUtils.isNotBlank(frontmatter.description())) {
                return frontmatter.description();
            }
            return relativePath().toString();
        }
    }

    /**
     * Returns the frontmatter timestamp.
     *
     * @return timestamp
     */
    public OffsetDateTime timestamp() {
        if (hasFrontmatter()) {
            return frontmatter.parsedTimestamp();
        }
        return null;
    }

    /**
     * Returns the HTML file name corresponding to the Markdown file.
     *
     * @return HTML file name
     */
    public String htmlFileName() {
        // TODO Manage the case "myfile.md.super.md".
        return Strings.CI.replace(fileName, MARKDOWN_EXTENSION, HTML_EXTENSION);
    }

    /**
     * Returns the HTML path corresponding to the Markdown file (with the file name).
     *
     * @return HTML path
     */
    public String htmlFilePath() {
        String markdownFilePath = relativePath.toString().replace('\\', '/');
        if (Strings.CI.endsWith(markdownFilePath, MARKDOWN_EXTENSION)) {
            return markdownFilePath.substring(0, markdownFilePath.length() - MARKDOWN_EXTENSION.length()) + HTML_EXTENSION;
        }
        return markdownFilePath + HTML_EXTENSION;
    }

}
