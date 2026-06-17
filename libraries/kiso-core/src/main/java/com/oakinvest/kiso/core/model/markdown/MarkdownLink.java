package com.oakinvest.kiso.core.model.markdown;

/**
 * Markdown link found in a document body.
 *
 * @param label link label visible in markdown
 * @param target raw link target
 * @param external whether the target points outside the bundle
 */
public record MarkdownLink(
        String label,
        String target,
        boolean external
) {
}
