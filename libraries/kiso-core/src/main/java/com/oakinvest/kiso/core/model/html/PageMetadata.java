package com.oakinvest.kiso.core.model.html;

import lombok.Builder;
import org.apache.commons.io.FilenameUtils;
import org.jspecify.annotations.Nullable;

import static com.oakinvest.kiso.core.util.FileExtensionsConstants.MARKDOWN_EXTENSION;

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
        return new PageMetadata(null, null, null, null, null);
    }

    /**
     * Returns Markdown filename.
     *
     * @return Markdown filename
     */
    public String markdownFileName() {
        return FilenameUtils.removeExtension(FilenameUtils.getName(absolutePath)) + MARKDOWN_EXTENSION;
    }

}
