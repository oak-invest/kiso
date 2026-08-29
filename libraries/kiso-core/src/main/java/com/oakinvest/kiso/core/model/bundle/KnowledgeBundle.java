package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A knowledge bundle: A self-contained, hierarchical collection of knowledge documents.
 *
 * @param rootBundle root bundle
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeBundle(
        Bundle rootBundle
) {

    /**
     * Returns all bundles.
     *
     * @return all bundles
     */
    public Stream<Bundle> bundles() {
        return rootBundle.flattenBundles();
    }

    /**
     * Returns all Markdown files.
     *
     * @return all Markdown files
     */
    public Stream<MarkdownFile> markdownFiles() {
        return rootBundle.flattenMarkdownFiles();
    }


    /**
     * Returns all unique tags used by Markdown files in this bundle.
     *
     * @return unique tags in their first-seen order
     */
    public List<String> tags() {
        return markdownFiles()
                .map(MarkdownFile::frontmatter)
                .filter(Objects::nonNull)
                .flatMap(frontmatter -> frontmatter.tags().stream())
                .distinct()
                .toList();
    }

    /**
     * Returns all unique tag slugs used by Markdown files in this bundle.
     *
     * @return unique tag slugs in their first-seen order
     */
    public List<String> tagSlugs() {
        return markdownFiles()
                .map(MarkdownFile::frontmatter)
                .filter(Objects::nonNull)
                .flatMap(frontmatter -> frontmatter.tagSlugs().stream())
                .distinct()
                .toList();
    }

}
