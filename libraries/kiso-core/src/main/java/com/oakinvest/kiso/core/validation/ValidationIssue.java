package com.oakinvest.kiso.core.validation;

import lombok.Builder;

import java.nio.file.Path;

import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Validation issue.
 *
 * @param severity validation severity
 * @param code     validation code
 * @param message  validation message
 * @param path     path of the file/directory where is the mistake
 */
@Builder
@SuppressWarnings("unused")
public record ValidationIssue(
        ValidationSeverity severity,
        ValidationCode code,
        String message,
        Path path
) {

    /**
     * Is an error.
     *
     * @return {@code true} if this issue is an error
     */
    public boolean isError() {
        return severity == ERROR;
    }

    /**
     * Is a warning.
     *
     * @return {@code true} if this issue is a warning
     */
    public boolean isWarning() {
        return severity == WARNING;
    }

}
