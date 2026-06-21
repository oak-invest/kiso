package com.oakinvest.kiso.core.model.bundle;

import lombok.Builder;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A knowledge bundle: A self-contained, hierarchical collection of knowledge documents.
 *
 * @param rootBundlePath root bundle path
 * @param rootBundle     root bundle
 * @param title          knowledge bundle title
 */
@Builder
@SuppressWarnings("unused")
public record KnowledgeBundle(
        Path rootBundlePath,
        Bundle rootBundle,
        // Kiso specific configuration (loaded in ./kiso files)
        String title
) {

    /**
     * Returns all bundles.
     *
     * @return all bundles
     */
    public Stream<Bundle> bundles() {
        return rootBundle.flatten();
    }

}
