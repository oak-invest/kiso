package com.oakinvest.kiso.core.rendering.model;

import lombok.Builder;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.util.FileExtensions.MARKDOWN_EXTENSION;

/**
 * Page metadata.
 *
 * @param title         the title of the page
 * @param description   the description of the page
 * @param absolutePath  the absolute path of the page
 * @param htmlPath      the HTML path relative to the generated site root
 * @param assetBasePath relative absolutePath from this page to the generated site root
 */
@Builder
@SuppressWarnings("unused")
public record PageMetadata(
        String title,
        String description,
        String absolutePath,
        String htmlPath,
        String assetBasePath
) {

    /**
     * Returns Markdown filename.
     *
     * @return Markdown filename
     */
    public String markdownFileName() {
        final String fileName = Path.of(absolutePath)
                .getFileName()
                .toString();

        final int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1) {
            return fileName + MARKDOWN_EXTENSION;
        }
        return fileName.substring(0, extensionIndex) + MARKDOWN_EXTENSION;
    }

}
