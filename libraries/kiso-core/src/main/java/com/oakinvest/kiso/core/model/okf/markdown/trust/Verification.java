package com.oakinvest.kiso.core.model.okf.markdown.trust;

import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Event confirming content against its sources or resource.
 *
 * @param by actor that verified the content
 * @param at ISO 8601 datetime of the verification
 */
@Builder
@SuppressWarnings("unused")
public record Verification(
        @Nullable Actor by,
        @Nullable String at
) {

    /**
     * Returns parsed verification time.
     *
     * @return verification time as OffsetDateTime, or null if the value is blank or not parsable
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
