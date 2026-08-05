package com.oakinvest.kiso.core.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

/**
 * Theme constants.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class ThemeConstants {

    /** List of standard DaisyUI themes. */
    @SuppressWarnings("SpellCheckingInspection")
    public static final Set<String> NAMES = Set.of(
            "light", "dark", "cupcake", "bumblebee", "emerald",
            "corporate", "synthwave", "retro", "cyberpunk", "valentine",
            "halloween", "garden", "forest", "aqua", "lofi",
            "pastel", "fantasy", "wireframe", "black", "luxury",
            "dracula", "cmyk", "autumn", "business", "acid",
            "lemonade", "night", "coffee", "winter", "dim",
            "nord", "sunset", "caramellatte", "abyss", "silk"
    );

    /**
     * Checks if the given theme name is a standard DaisyUI theme.
     *
     * @param themeName theme name
     * @return true if it exists
     */
    public static boolean contains(@Nullable final String themeName) {
        if (StringUtils.isBlank(themeName)) {
            return false;
        }
        return NAMES.contains(themeName.strip().toLowerCase(Locale.ROOT));
    }

}
