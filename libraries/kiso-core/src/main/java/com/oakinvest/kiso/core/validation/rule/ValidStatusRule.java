package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.util.types.LifecycleStatus;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.contants.FrontmatterConstants.STATUS_KEY;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_LIFE_CYCLE_STATUS;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Warn when the status is not one of: draft, stable, deprecated.
 */
public class ValidStatusRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        // If the status is not present, we assume it is stable and do not warn
        if (!markdownFile.frontmatter().extraFields().containsKey(STATUS_KEY)) {
            return List.of();
        }

        // If the status is present and valid, we do not warn
        final Object status = markdownFile.frontmatter().extraFields().get(STATUS_KEY);
        if (status != null && LifecycleStatus.exists(status.toString())) {
            return List.of();
        }

        // There is a problem.
        return List.of(ValidationIssue.builder()
                .severity(WARNING)
                .code(INVALID_LIFE_CYCLE_STATUS)
                .message("The status is not one of: draft, stable, deprecated")
                .path(markdownFile.relativePath())
                .build());
    }

}
