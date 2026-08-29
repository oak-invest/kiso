package com.oakinvest.kiso.core.tool;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility for wrapping text into multiple lines with word preservation.
 * Splits text into lines while preserving words and supporting truncation.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class TextWrapper {

    /**
     * Ellipsis character for text truncation.
     */
    private static final String ELLIPSIS = "…";

    /**
     * Default maximum characters per line.
     */
    private static final int DEFAULT_MAX_LINE_LENGTH = 50;

    /**
     * Default maximum number of lines.
     */
    private static final int DEFAULT_MAX_LINES = 3;

    /**
     * Wraps text into multiple lines using default settings.
     *
     * @param text the text to wrap
     * @return list of wrapped text lines
     */
    public static List<String> wrap(final String text) {
        return wrap(text, DEFAULT_MAX_LINE_LENGTH, DEFAULT_MAX_LINES);
    }

    /**
     * Wraps text into multiple lines with specified maximum characters and lines.
     * Preserves words and appends ellipsis when truncated.
     *
     * @param text          the text to wrap
     * @param maxLineLength maximum characters per line (including ellipsis if truncated)
     * @param maxLines      maximum number of lines to return
     * @return list of wrapped text lines
     */
    public static List<String> wrap(final String text, final int maxLineLength, final int maxLines) {
        if (StringUtils.isBlank(text)) {
            return List.of();
        }

        final String normalizedText = text.trim().replaceAll("\\s+", " ");
        final String wrappedText = WordUtils.wrap(normalizedText, maxLineLength, "\n", false);
        final List<String> lines = new ArrayList<>(Arrays.asList(wrappedText.split("\n")));

        truncateLongLines(lines, maxLineLength);

        if (lines.size() > maxLines) {
            return truncateToMaxLines(lines, maxLineLength, maxLines);
        }

        return lines;
    }

    /**
     * Truncates every line exceeding the maximum line length.
     *
     * @param lines         the lines to inspect
     * @param maxLineLength maximum line length including ellipsis
     */
    private static void truncateLongLines(final List<String> lines, final int maxLineLength) {
        for (int index = 0; index < lines.size(); index++) {
            final String line = lines.get(index);
            if (line.length() > maxLineLength) {
                lines.set(index, truncateLine(line, maxLineLength));
            }
        }
    }

    /**
     * Truncates text to maximum line length and adds ellipsis.
     *
     * @param line          the line to truncate
     * @param maxLineLength maximum line length including ellipsis
     * @return truncated line with ellipsis
     */
    private static String truncateLine(final String line, final int maxLineLength) {
        if (maxLineLength <= ELLIPSIS.length()) {
            return ELLIPSIS;
        }
        return StringUtils.substring(line, 0, maxLineLength - ELLIPSIS.length()) + ELLIPSIS;
    }

    /**
     * Truncates a line list to maximum lines and appends ellipsis to the last line.
     *
     * @param lines         the lines to truncate
     * @param maxLineLength maximum line length including ellipsis
     * @param maxLines      maximum number of lines
     * @return truncated lines list
     */
    private static List<String> truncateToMaxLines(final List<String> lines, final int maxLineLength, final int maxLines) {
        if (lines == null) {
            return List.of();
        }

        final List<String> truncated = new ArrayList<>(lines.subList(0, maxLines));
        final String lastLine = truncated.get(maxLines - 1);

        if (!lastLine.endsWith(ELLIPSIS)) {
            String truncatedLast = lastLine;
            if (lastLine.length() + ELLIPSIS.length() > maxLineLength) {
                truncatedLast = StringUtils.substring(lastLine, 0, Math.max(0, lastLine.length() - ELLIPSIS.length()));
            }
            truncated.set(maxLines - 1, truncatedLast + ELLIPSIS);
        }

        return truncated;
    }

}
