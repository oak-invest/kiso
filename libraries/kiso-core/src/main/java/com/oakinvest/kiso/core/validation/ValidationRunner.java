package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.rule.AttestedComputationRule;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import com.oakinvest.kiso.core.validation.rule.DuplicateSourceIdRule;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import com.oakinvest.kiso.core.validation.rule.MarkdownFileRule;
import com.oakinvest.kiso.core.validation.rule.SourceFootnoteRule;
import com.oakinvest.kiso.core.validation.rule.TrustEventRule;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import com.oakinvest.kiso.core.validation.rule.ValidLogRule;
import com.oakinvest.kiso.core.validation.rule.ValidSourceRule;
import com.oakinvest.kiso.core.validation.rule.ValidStaleAfterRule;
import com.oakinvest.kiso.core.validation.rule.ValidStatusRule;
import com.oakinvest.kiso.core.validation.rule.ValidUsageWindowRule;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

/**
 * Validation runner.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class ValidationRunner {

    /** Markdown file rules. */
    private static final List<MarkdownFileRule> MARKDOWN_FILE_RULES = List.of(
            new BrokenLinkRule(),
            new AttestedComputationRule(),
            new DuplicateSourceIdRule(),
            new EncodingRule(),
            new SourceFootnoteRule(),
            new TrustEventRule(),
            new ValidFrontmatterRule(),
            new ValidLogRule(),
            new ValidSourceRule(),
            new ValidStaleAfterRule(),
            new ValidStatusRule(),
            new ValidUsageWindowRule()
    );

    /**
     * Validate a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return validation report
     */
    public static ValidationReport runValidation(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        final List<ValidationIssue> issues = knowledgeBundle.markdownFiles()
                .flatMap(markdownFile -> MARKDOWN_FILE_RULES.stream()
                        .flatMap(rule -> rule.validate(knowledgeBundle, markdownFile).stream()))
                .toList();

        return ValidationReport.builder()
                .issues(issues)
                .build();
    }

}
