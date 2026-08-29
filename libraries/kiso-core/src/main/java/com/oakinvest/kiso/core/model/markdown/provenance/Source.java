package com.oakinvest.kiso.core.model.markdown.provenance;

import com.oakinvest.kiso.core.model.markdown.Actor;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * A material a concept derives from, external or internal to the bundle, recorded in the sources frontmatter field.
 *
 * @param id           stable key used to attribute individual claims
 * @param resource     concrete artifact or scope descriptor the concept derives from
 * @param title        human-readable source label
 * @param author       actor that produced the source
 * @param usageCount   number of source exercises over its usage window
 * @param lastModified source last modification date in YYYY-MM-DD format
 * @param usageWindow  source-specific usage window overriding the shared one
 */
@Builder
@SuppressWarnings("unused")
public record Source(
        @Nullable String id,
        @Nullable String resource,
        @Nullable String title,
        @Nullable Actor author,
        @Nullable Long usageCount,
        @Nullable String lastModified,
        @Nullable UsageWindow usageWindow
) {

    /**
     * Returns true when this source has an author.
     *
     * @return true when this source has an author
     */
    public boolean hasAuthor() {
        return author != null;
    }

    /**
     * Returns parsed source last modification date.
     *
     * @return parsed source last modification date, or null if missing or invalid
     */
    public @Nullable LocalDate parsedLastModified() {
        if (StringUtils.isBlank(lastModified)) {
            return null;
        }

        try {
            return LocalDate.parse(lastModified);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

}
