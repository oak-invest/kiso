package com.oakinvest.kiso.core.validation;

import lombok.Builder;

import java.util.List;

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
     * Has errors.
     *
     * @return true is there are errors
     */
    public boolean hasErrors() {
        return issues.stream().anyMatch(ValidationIssue::isError);
    }

}
