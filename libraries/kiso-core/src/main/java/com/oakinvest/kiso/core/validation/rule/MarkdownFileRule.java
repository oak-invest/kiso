package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Markdown file rule.
 */
public interface MarkdownFileRule {

    /**
     * Returns validation issues of a markdown file.
     *
     * @param bundle       directory - Bundle
     * @param markdownFile file - Markdown file
     * @return list of issues
     */
    List<ValidationIssue> validate(Bundle bundle, MarkdownFile markdownFile);

    /**
     * Checks whether the given string is a valid ISO 8601 offset date-time.
     *
     * @param value the string to validate
     * @return true if the string is a valid ISO 8601 offset date-time, false otherwise
     */
    default boolean isValidISO8601OffsetDateTime(final String value) {
        try {
            OffsetDateTime.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks whether the given string is a valid ISO 8601 local date.
     *
     * @param value the string to validate
     * @return true if the string is a valid ISO 8601 local date, false otherwise
     */
    default boolean isValidISO8601LocalDate(final String value) {
        try {
            LocalDate.parse(value, ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
