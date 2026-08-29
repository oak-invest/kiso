package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.provenance.UsageWindow;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_USAGE_WINDOW_FROM;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_USAGE_WINDOW_TO;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Usage window rule.
 * Validate usage_window.from and usage_window.to as YYYY-MM-DD when present.
 */
public class ValidUsageWindowRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        final List<ValidationIssue> issues = new LinkedList<>();
        final UsageWindow usageWindow = markdownFile.frontmatter().usageWindow();

        if (usageWindow == null) {
            return issues;
        }

        if (StringUtils.isNotBlank(usageWindow.from())
                && !isValidISO8601LocalDate(usageWindow.from())) {
            issues.add(ValidationIssue.builder()
                    .severity(ERROR)
                    .code(INVALID_USAGE_WINDOW_FROM)
                    .message("Invalid usage_window.from field in frontmatter - Not an ISO 8601 date")
                    .path(markdownFile.relativePath())
                    .build());
        }

        if (StringUtils.isNotBlank(usageWindow.to())
                && !isValidISO8601LocalDate(usageWindow.to())) {
            issues.add(ValidationIssue.builder()
                    .severity(ERROR)
                    .code(INVALID_USAGE_WINDOW_TO)
                    .message("Invalid usage_window.to field in frontmatter - Not an ISO 8601 date")
                    .path(markdownFile.relativePath())
                    .build());
        }

        return issues;
    }

}
