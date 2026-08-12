package com.oakinvest.kiso.core.model.okf.markdown.trust;

import com.oakinvest.kiso.core.model.okf.markdown.Actor;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Trust event recording who performed an action and when it happened.
 *
 * @param by actor that performed the trust event
 * @param at ISO 8601 datetime of the trust event
 */
@Builder
@SuppressWarnings("unused")
public record TrustEvent(
        @Nullable Actor by,
        @Nullable String at
) {

    /**
     * Returns parsed event time.
     *
     * @return event time as OffsetDateTime, or null if the value is blank or not parsable
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
