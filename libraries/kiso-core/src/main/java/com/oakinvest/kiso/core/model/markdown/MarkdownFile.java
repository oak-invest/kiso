package com.oakinvest.kiso.core.model.markdown;

import java.nio.file.Path;

/**
 * Markdown file discovered inside a knowledge bundle.
 *
 * @param path absolute or execution-relative file path
 * @param relativePath path relative to the bundle root directory
 * @param kind semantic kind of Markdown file
 * @param source original Markdown source
 */
public record MarkdownFile(
        Path path,
        Path relativePath,
        MarkdownFileKind kind,
        String source
) {
}
