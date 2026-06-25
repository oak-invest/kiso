package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.FileExtensions.HTML_EXTENSION;
import static com.oakinvest.kiso.core.util.FileExtensions.MARKDOWN_EXTENSION;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param fileName     file name
 * @param kind         file kind
 * @param absolutePath absolute path
 * @param relativePath relative path to the root bundle
 * @param frontmatter  frontmatter metadata
 * @param content      original Markdown content (without frontmatter)
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        String fileName,
        MarkdownFileKind kind,
        Path absolutePath,
        Path relativePath,
        Frontmatter frontmatter,
        String content
) {

    /**
     * Returns the page title.
     *
     * @return page title
     */
    public String title() {
        if (frontmatter != null && frontmatter.title() != null) {
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
            return StringUtils.normalizeSpace(frontmatter.description());
        }
    }

    /**
     * Returns the HTML file name corresponding to the Markdown file.
     *
     * @return HTML file name
     */
    public String htmlFileName() {
        // TODO Manage the case myfile.md.super.md.
        return Strings.CI.replace(fileName, MARKDOWN_EXTENSION, HTML_EXTENSION);
    }

}
