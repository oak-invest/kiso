package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;

import java.nio.file.Path;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param path         absolute or execution-relative file path
 * @param relativePath path relative to the bundle rootBundleDirectory directory
 * @param frontmatter  parsed frontmatter metadata
 * @param content      original Markdown content (without frontmatter)
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        Path path,
        Path relativePath,
        Frontmatter frontmatter,
        String content
) {

    /**
     * Returns the Markdown file kind based on the file path.
     *
     * @return Markdown file kind
     */
    public MarkdownFileKind kind() {
        return MarkdownFileKind.from(path);
    }

}
