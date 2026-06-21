package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;
import org.apache.commons.lang3.Strings;

import java.nio.file.Path;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param fileName     file fileName
 * @param path         absolute or execution-relative file path
 * @param relativePath path relative to the bundle rootBundleDirectory directory
 * @param frontmatter  parsed frontmatter metadata
 * @param content      original Markdown content (without frontmatter)
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        String fileName,
        Path path,
        Path relativePath,
        Frontmatter frontmatter,
        String content
) {

    /**
     * Returns the HTML file name corresponding to this Markdown file.
     *
     * @return HTML file name
     */
    public String htmlFileName() {
        return Strings.CI.replace(fileName, ".md", ".html");
    }

    /**
     * Returns the Markdown file kind based on the file path.
     *
     * @return Markdown file kind
     */
    public MarkdownFileKind kind() {
        return MarkdownFileKind.from(path);
    }

}
