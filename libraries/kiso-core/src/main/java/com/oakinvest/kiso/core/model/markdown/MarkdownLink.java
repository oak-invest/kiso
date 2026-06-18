package com.oakinvest.kiso.core.model.markdown;

import lombok.Builder;

/**
 * Markdown link found in a document body.
 *
 * @param label link label visible in markdown
 * @param target raw link target
 * @param external whether the target points outside the bundle
 */
@Builder
@SuppressWarnings("unused")
public record MarkdownLink(
        String label,
        String target,
        boolean external
) {
}
