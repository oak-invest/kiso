package com.oakinvest.kiso.core.validation;

import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Validation report.
 *
 * @param issues list of validation issues
 */
@Builder
@SuppressWarnings("unused")
public record ValidationReport(
        List<ValidationIssue> issues
) {

    /**
     * Creates a validation report with safe default values.
     */
    public ValidationReport {
        issues = Objects.requireNonNullElse(issues, List.of());
    }

    /**
     * Has errors.
     *
     * @return {@code true} is there are errors
     */
    public boolean hasErrors() {
        return issues.stream().anyMatch(ValidationIssue::isError);
    }

}
