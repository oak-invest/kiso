package com.oakinvest.kiso.core.renderer.util;

import lombok.Builder;

/**
 * Page metadata.
 *
 * @param title       the title of the page
 * @param description the description of the page
 * @param path        the path of the page
 */
@Builder
@SuppressWarnings("unused")
public record PageMetadata(
        String title,
        String description,
        String path
) {
}
