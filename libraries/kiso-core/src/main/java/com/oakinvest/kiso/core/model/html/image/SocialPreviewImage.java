package com.oakinvest.kiso.core.model.html.image;

import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Social preview image data for Open Graph and Twitter Cards.
 * Contains the information needed to render SVG and PNG preview images.
 *
 * @param siteName         the name of the site
 * @param titleLines       pre-wrapped title lines for rendering
 * @param descriptionLines pre-wrapped description lines for rendering
 * @param url              the canonical URL of the page
 */
@Builder
@SuppressWarnings("unused")
public record SocialPreviewImage(
        String siteName,
        List<String> titleLines,
        List<String> descriptionLines,
        String url
) {

    /**
     * Constructor.
     */
    public SocialPreviewImage {
        titleLines = Objects.requireNonNullElse(titleLines, List.of());
        descriptionLines = Objects.requireNonNullElse(descriptionLines, List.of());
    }

}
