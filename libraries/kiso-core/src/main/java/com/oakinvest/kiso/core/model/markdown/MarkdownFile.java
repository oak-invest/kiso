package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;

import java.nio.file.Path;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param path         absolute or execution-relative file path
 * @param relativePath path relative to the bundle rootBundleDirectory directory
 * @param content      original Markdown content
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownFile(
        Path path,
        Path relativePath,
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
