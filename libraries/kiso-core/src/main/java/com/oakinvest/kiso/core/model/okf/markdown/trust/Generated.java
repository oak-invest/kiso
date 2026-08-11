package com.oakinvest.kiso.core.model.okf.markdown.trust;

import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Generated records how the current content was produced.
 *
 * @param by actor that generated the content
 * @param at ISO 8601 datetime marking the content's last meaningful change
 */
@Builder
@SuppressWarnings("unused")
public record Generated(
        Actor by,
        @Nullable String at
) {

    /**
     * Returns parsed generation time.
     *
     * @return generation time as OffsetDateTime, or null if the value is blank or not parsable
     */
    public @Nullable OffsetDateTime parsedAt() {
        if (StringUtils.isBlank(at)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(at);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

}
