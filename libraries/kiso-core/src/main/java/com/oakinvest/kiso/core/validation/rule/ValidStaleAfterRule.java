package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.util.List;

import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.STALE_AFTER_KEY;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_STALE_AFTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Warn when stale_after is present but is not a valid YYYY-MM-DD date.
 */
public class ValidStaleAfterRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        if (!markdownFile.frontmatter().extraFields().containsKey(STALE_AFTER_KEY)) {
            return List.of();
        }

        final String staleAfter = markdownFile.frontmatter().staleAfter();
        if (staleAfter != null
                && staleAfter.matches("\\d{4}-\\d{2}-\\d{2}")
                && isValidISO8601LocalDate(staleAfter)) {
            return List.of();
        }

        return List.of(ValidationIssue.builder()
                .severity(WARNING)
                .code(INVALID_STALE_AFTER)
                .message("The stale_after field is not a valid YYYY-MM-DD date")
                .path(markdownFile.relativePath())
                .build());
    }

}
