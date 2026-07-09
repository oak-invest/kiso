package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;

/**
 * A bundle is a directory tree of markdown files.
 *
 * @param name          bundle name (example: "references/joins")
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
    public Stream<Bundle> flattenBundles() {
        return Stream.concat(
                Stream.of(this),
                childBundles.stream().flatMap(Bundle::flattenBundles)
        );
    }

    /**
     * Returns all Markdown files in this knowledge bundle as a flat stream.
     *
     * @return a flat stream of all Markdown files in this knowledge bundle
     */
    public Stream<MarkdownFile> flattenMarkdownFiles() {
        return Stream.concat(
                markdownFiles.stream(),
                childBundles.stream().flatMap(Bundle::flattenMarkdownFiles)
        );
    }

    /**
     * Returns true if the bundle has any concept files.
     *
     * @return true if it has concept file
     */
    public boolean hasContent() {
        return markdownFiles.stream()
                .anyMatch(markdownFile -> markdownFile.kind().equals(CONCEPT));
    }

    /**
     * Returns the bundle name without its parent path.
     *
     * @return the bundle name without its parent path
     */
    public String simpleName() {
        if (!StringUtils.contains(name(), '/')) {
            return name();
        }
        return StringUtils.substringAfterLast(name(), "/");
    }

    /**
     * Returns the index file of the bundle.
     *
     * @return index file
     */
    public Optional<MarkdownFile> getIndexFile() {
        return markdownFiles.stream()
                .filter(markdownFile -> markdownFile.kind().equals(INDEX))
                .findFirst();
    }

    /**
     * Returns true if the bundle has no child bundles and no markdown files.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return childBundles.isEmpty() && markdownFiles.isEmpty();
    }

}
