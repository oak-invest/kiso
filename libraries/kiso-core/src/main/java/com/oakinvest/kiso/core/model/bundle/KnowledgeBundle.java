package com.oakinvest.kiso.core.model.bundle;

import lombok.Builder;

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
        return rootBundle.flatten();
    }

}
