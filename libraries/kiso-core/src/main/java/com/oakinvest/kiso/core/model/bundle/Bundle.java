package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;

import java.nio.file.Path;
import java.util.List;

/**
 * A bundle is a directory tree of markdown files.
 *
 * @param path                   absolute or execution-relative directory path
 * @param relativePath           path relative to the bundle rootBundleDirectory directory
 * @param childBundleDirectories direct child directories
 * @param markdownFiles          direct Markdown files
 */
@Builder
@SuppressWarnings("unused")
public record Bundle(
        Path path,
        Path relativePath,
        List<Bundle> childBundleDirectories,
        List<MarkdownFile> markdownFiles
) {
}
