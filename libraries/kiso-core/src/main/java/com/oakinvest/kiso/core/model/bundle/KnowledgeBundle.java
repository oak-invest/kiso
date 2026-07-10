package com.oakinvest.kiso.core.model.bundle;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.Builder;

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
        if (siteConfiguration == null) {
            siteConfiguration = SiteConfiguration.empty();
        }
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

}
