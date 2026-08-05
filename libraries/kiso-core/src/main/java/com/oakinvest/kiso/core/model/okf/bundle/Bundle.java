package com.oakinvest.kiso.core.model.okf.bundle;

import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX;

/**
 * A bundle is a directory tree of markdown files.
 * <p>
 * Example for the root bundle:
 * <pre>{@code
 * name: index
 * absolutePath: /home/straumat/kiso/libraries/kiso-core/target/test-classes/kb-google-example-v0.1
 * relativePath:
 * simpleName: index
 * }</pre>
 * <p>
 * Example for the references/joins:
 * <pre>{@code
 * name: references/joins
 * absolutePath: /home/straumat/IdeaProjects/oak-invest/kiso/libraries/kiso-core/target/test-classes/kb-google-example-v0.1/references/joins
 * relativePath: references/joins
 * simpleName: joins
 * }</pre>
 *
 * @param name          bundle name (example: "index" for root bundle or "references/joins" for references/joins bundle)
 * @param absolutePath  absolute path on the operating system (example: "/home/straumat/kiso/libraries/kiso-core/target/test-classes/kb-google-example-v0.1")
 * @param relativePath  relative path to the root bundle (example: "references/joins" for references/joins bundle, empty for root bundle)
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
     * Creates a bundle with safe default values.
     */
    public Bundle {
        childBundles = Objects.requireNonNullElse(childBundles, List.of());
        markdownFiles = Objects.requireNonNullElse(markdownFiles, List.of());
    }

    /**
     * Returns the bundle name without its parent path.
     * Bundle "root" simpleName() : returns "index"
     * Bundle "references/joins" simpleName() : returns "joins"
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
     * Returns true if the bundle has any concept files.
     *
     * @return {@code true} if it has a concept file
     */
    public boolean hasContent() {
        return markdownFiles.stream().anyMatch(markdownFile -> markdownFile.kind().equals(CONCEPT));
    }

    /**
     * Returns true if the bundle has an index file.
     *
     * @return {@code true} if it has an index file
     */
    public boolean hasIndexFile() {
        return getIndexFile().isPresent();
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
