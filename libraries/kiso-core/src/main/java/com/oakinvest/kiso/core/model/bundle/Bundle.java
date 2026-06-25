package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * A bundle is a directory tree of markdown files.
 *
 * @param name          bundle name
 * @param absolutePath  absolute path
 * @param relativePath  relative path to the root bundle
 * @param childBundles  child bundles
 * @param markdownFiles direct Markdown files
 */
@Builder
@SuppressWarnings("unused")
public record Bundle(
        String name,
        Path absolutePath,
        Path relativePath,
        List<Bundle> childBundles,
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
                childBundles.stream().flatMap(Bundle::flatten)
        );
    }

}
