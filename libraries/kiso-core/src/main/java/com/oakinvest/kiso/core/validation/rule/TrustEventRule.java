package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.trust.TrustEvent;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_GENERATED_AT;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_VERIFIED_AT;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_GENERATED_AT;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_GENERATED_BY;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_VERIFIED_AT;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_VERIFIED_BY;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Trust event rule.
 * When generated is present:
 * - Require generated.by.
 * - Require generated.at.
 * - Validate generated.at as ISO 8601.
 * When verified is present, for every verification event:
 * - Require verified.by.
 * - Require verified.at.
 * - Validate verified.at as ISO 8601.
 */
public class TrustEventRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        final List<ValidationIssue> issues = new LinkedList<>();

        // When generated is present:
        if (markdownFile.frontmatter().generated() != null) {
            // Require generated.by.
            if (markdownFile.frontmatter().generated().by() == null
                    || StringUtils.isBlank(markdownFile.frontmatter().generated().by().identifier())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_GENERATED_BY)
                        .message("Missing generated.by field in frontmatter")
                        .path(markdownFile.relativePath())
                        .build());
            }
            // Require generated.at.
            if (StringUtils.isBlank(markdownFile.frontmatter().generated().at())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_GENERATED_AT)
                        .message("Missing generated.at field in frontmatter")
                        .path(markdownFile.relativePath())
                        .build());
            } else {
                // Validate generated.at as ISO 8601.
                if (!isValidISO8601OffsetDateTime(markdownFile.frontmatter().generated().at())) {
                    issues.add(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_GENERATED_AT)
                            .message("Invalid generated.at field in frontmatter - Not an ISO 8601 datetime format")
                            .path(markdownFile.relativePath())
                            .build());
                }
            }
        }

        // When verified is present, for every verification event:
        for (TrustEvent trustEvent : markdownFile.frontmatter().verified()) {
            // Require verified.by.
            if (trustEvent.by() == null || StringUtils.isBlank(trustEvent.by().identifier())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_VERIFIED_BY)
                        .message("Missing verified.by field in frontmatter for verified event")
                        .path(markdownFile.relativePath())
                        .build());
            }
            // Require verified.at.
            if (StringUtils.isBlank(trustEvent.at())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_VERIFIED_AT)
                        .message("Missing verified.at field in frontmatter for verified event")
                        .path(markdownFile.relativePath())
                        .build());
            } else {
                // Validate verified.at as ISO 8601.
                if (!isValidISO8601OffsetDateTime(trustEvent.at())) {
                    issues.add(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_VERIFIED_AT)
                            .message("Invalid verified.at field in frontmatter - Not an ISO 8601 datetime format")
                            .path(markdownFile.relativePath())
                            .build());
                }
            }
        }

        return issues;
    }

}
