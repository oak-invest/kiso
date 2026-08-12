package com.oakinvest.kiso.core.model.okf.bundle;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.Builder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A knowledge bundle: A self-contained, hierarchical collection of knowledge documents.
 *
 * @param rootBundle        root bundle
 * @param siteConfiguration site configuration
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeBundle(
        Bundle rootBundle,
        SiteConfiguration siteConfiguration
) {

    /**
     * Creates a knowledge bundle with safe default values.
     */
    public KnowledgeBundle {
        siteConfiguration = Objects.requireNonNullElse(siteConfiguration, SiteConfiguration.empty());
    }

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
        Set<String> tags = new LinkedHashSet<>();
        markdownFiles()
                .map(MarkdownFile::frontmatter)
                .filter(Objects::nonNull)
                .flatMap(frontmatter -> frontmatter.tags().stream())
                .forEach(tags::add);
        return List.copyOf(tags);
    }

}
