package com.oakinvest.kiso.core.tool;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Utility class for generating URL-safe tags.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class TagNormalizer {

    /** Fallback normalize when a tag does not contain URL-safe characters. */
    private static final String DEFAULT_TAG = "tag";

    /**
     * Returns a URL-safe tag.
     *
     * @param tag original tag
     * @return URL-safe tag
     */
    public static String normalize(final String tag) {
        if (StringUtils.isBlank(tag)) {
            return DEFAULT_TAG;
        }

        String slug = Normalizer.normalize(tag, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (StringUtils.isBlank(slug)) {
            return DEFAULT_TAG;
        }

        return slug;
    }

}
