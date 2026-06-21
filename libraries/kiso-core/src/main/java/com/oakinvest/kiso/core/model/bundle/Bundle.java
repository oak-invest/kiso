package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * A bundle is a directory tree of markdown files.
 *
 * @param name                   bundle fileName
 * @param path                   absolute or execution-relative directory path
 * @param relativePath           path relative to the bundle rootBundleDirectory directory
 * @param childBundleDirectories direct child directories
 * @param markdownFiles          direct Markdown files
 */
@Builder
@SuppressWarnings("unused")
public record Bundle(
        String name,
        Path path,
        Path relativePath,
        List<Bundle> childBundleDirectories,
        List<MarkdownFile> markdownFiles
) {

    /**
     * Returns all bundles contained in this knowledge bundle as a flat stream.
     *
     * @return a flat stream of all bundles in this knowledge bundle
     */
    public Stream<Bundle> flatten() {
        return Stream.concat(
                Stream.of(this),
                childBundleDirectories.stream().flatMap(Bundle::flatten)
        );
    }

}
