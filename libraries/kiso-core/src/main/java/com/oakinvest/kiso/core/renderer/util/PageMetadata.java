package com.oakinvest.kiso.core.renderer.util;

import lombok.Builder;

import java.nio.file.Path;

/**
 * Page metadata.
 *
 * @param title         the title of the page
 * @param description   the description of the page
 * @param path          the path of the page
 * @param assetBasePath relative path from this page to the generated site root
 */
@Builder
@SuppressWarnings("unused")
public record PageMetadata(
        String title,
        String description,
        String path,
        String assetBasePath
) {

    /**
     * Returns Markdown filename.
     *
     * @return Markdown filename
     */
    public String markdownFileName() {
        String fileName = Path.of(path)
                .getFileName()
                .toString();

        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1) {
            return fileName + ".md";
        }
        return fileName.substring(0, extensionIndex) + ".md";
    }

}
