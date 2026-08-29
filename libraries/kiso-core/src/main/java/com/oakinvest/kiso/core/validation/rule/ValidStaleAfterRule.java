package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.STALE_AFTER_KEY;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_STALE_AFTER;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Warn when stale_after is present but is not a valid YYYY-MM-DD date.
 */
public class ValidStaleAfterRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

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
