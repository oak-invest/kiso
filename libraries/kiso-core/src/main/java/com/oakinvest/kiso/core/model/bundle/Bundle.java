package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * A self-contained, hierarchical collection of knowledge documents. The unit of distribution.
 * <p>
 * Example for the root bundle:
 * - bundle.name = index
 * - bundle.simpleName = index
 * - bundle.absolutePath = /home/.../kb-google-example-v0.1
 * - bundle.relativePath =
 * <p>
 * Example for the references/joins:
 * - bundle.name = references/joins
 * - bundle.simpleName = joins
 * - bundle.absolutePath = /home/.../kb-google-example-v0.1/references/joins
 * - bundle.relativePath = references/joins
 *
 * @param name          bundle name (example: "index" for a root bundle or "references/joins" for a references/joins bundle)
 * @param absolutePath  absolute path on the operating system (example: "/home/.../kb-google-example-v0.1")
 * @param relativePath  relative path to the root bundle (example: "references/joins" for a references/joins bundle, empty for a root bundle)
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
     * Constructor with safe default values.
     */
    public Bundle {
        childBundles = Objects.requireNonNullElse(childBundles, List.of());
        markdownFiles = Objects.requireNonNullElse(markdownFiles, List.of());
    }

    /**
     * Returns the bundle name without its parent path.
     * Bundle "root" simpleName(): returns "index"
     * Bundle "references/joins" simpleName(): returns "joins"
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
     * Returns true if the bundle has any concept files.
     *
     * @return {@code true} if it has a concept file
     */
    public boolean hasContent() {
        return markdownFiles.stream()
                .anyMatch(markdownFile -> markdownFile.kind().equals(CONCEPT));
    }

    /**
     * Returns true if the bundle has no child bundles and no markdown files.
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty() {
        return childBundles.isEmpty() && markdownFiles.isEmpty();
    }

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

}
