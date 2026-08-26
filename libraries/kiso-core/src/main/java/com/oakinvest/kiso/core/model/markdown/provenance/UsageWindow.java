package com.oakinvest.kiso.core.model.markdown.provenance;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Date range that frames usage counts.
 *
 * @param from usage window start date in YYYY-MM-DD format
 * @param to   usage window end date in YYYY-MM-DD format
 */
@Builder
@SuppressWarnings("unused")
public record UsageWindow(
        @Nullable String from,
        @Nullable String to
) {

    /**
     * Returns parsed date.
     *
     * @param value date value
     * @return parsed date, or null if missing or invalid
     */
    private static @Nullable LocalDate parsedDate(@Nullable final String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Returns parsed start date.
     *
     * @return parsed start date, or null if missing or invalid
     */
    public @Nullable LocalDate parsedFrom() {
        return parsedDate(from);
    }

    /**
     * Returns parsed end date.
     *
     * @return parsed end date, or null if missing or invalid
     */
    public @Nullable LocalDate parsedTo() {
        return parsedDate(to);
    }

}
