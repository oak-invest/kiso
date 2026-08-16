package com.oakinvest.kiso.core.model.html.util;

import lombok.Builder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.MARKDOWN_EXTENSION;
import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.PNG_EXTENSION;

/**
 * Page metadata.
 *
 * @param title         the title of the page
 * @param description   the description of the page
 * @param absolutePath  the absolute path of the page
 * @param htmlPath      the HTML path relative to the generated site root
 * @param assetBasePath relative base Path from this page to the generated site root
 */
@Builder
@SuppressWarnings("unused")
public record PageMetadata(
        @Nullable String title,
        @Nullable String description,
        String absolutePath,
        String htmlPath,
        String assetBasePath
) {

    /**
     * Returns empty page metadata.
     *
     * @return empty page metadata
     */
    public static PageMetadata empty() {
        return PageMetadata.builder()
                .build();
    }

    /**
     * Returns the path to the home page (index.html) relative to the generated site root.
     *
     * @return the path to the home page (index.html) relative to the generated site root
     */
    public String homePath() {
        if (StringUtils.isBlank(assetBasePath)) {
            return "index.html";
        } else {
            return assetBasePath + "index.html";
        }
    }

    /**
     * Returns Markdown filename.
     *
     * @return Markdown filename
     */
    public String markdownFilename() {
        return FilenameUtils.removeExtension(FilenameUtils.getName(absolutePath)) + MARKDOWN_EXTENSION;
    }

    /**
     * Returns the social preview image HTML path.
     *
     * @return the social preview image HTML path
     */
    public String socialPreviewImagePath() {
        return FilenameUtils.removeExtension(htmlPath) + PNG_EXTENSION;
    }

    /**
     * Returns the HTML directory path.
     *
     * @return HTML directory path
     */
    public String htmlDirectoryPath() {
        return StringUtils.defaultIfEmpty(FilenameUtils.getPath(htmlPath), "/");
    }

}
