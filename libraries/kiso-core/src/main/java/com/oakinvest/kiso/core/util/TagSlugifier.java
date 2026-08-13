package com.oakinvest.kiso.core.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Slugifier for tag URLs and generated tag page filenames.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class TagSlugifier {

    /** Fallback slug when a tag does not contain URL-safe characters. */
    private static final String DEFAULT_SLUG = "tag";

    /**
     * Returns a URL-safe slug for a tag.
     *
     * @param tag original tag
     * @return URL-safe slug
     */
    public static String slug(final String tag) {
        if (StringUtils.isBlank(tag)) {
            return DEFAULT_SLUG;
        }

        String slug = Normalizer.normalize(tag, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (StringUtils.isBlank(slug)) {
            return DEFAULT_SLUG;
        }

        return slug;
    }

}
