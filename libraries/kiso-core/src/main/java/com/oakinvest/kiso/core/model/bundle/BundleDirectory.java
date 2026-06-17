package com.oakinvest.kiso.core.model.bundle;

import java.nio.file.Path;
import java.util.List;

/**
 * Directory discovered inside a knowledge bundle.
 *
 * @param path absolute or execution-relative directory path
 * @param relativePath path relative to the bundle root directory
 * @param childDirectories direct child directories
 * @param markdownFiles direct Markdown files
 */
public record BundleDirectory(
        Path path,
        Path relativePath,
        List<Path> childDirectories,
        List<Path> markdownFiles
) {
}
