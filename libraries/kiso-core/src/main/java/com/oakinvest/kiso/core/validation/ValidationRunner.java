package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.rule.AttestedComputationRule;
import com.oakinvest.kiso.core.validation.rule.BrokenLinkRule;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import com.oakinvest.kiso.core.validation.rule.MarkdownFileRule;
import com.oakinvest.kiso.core.validation.rule.TrustEventRule;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
import com.oakinvest.kiso.core.validation.rule.ValidLogRule;
import com.oakinvest.kiso.core.validation.rule.ValidSourceRule;
import com.oakinvest.kiso.core.validation.rule.ValidUsageWindowRule;
import lombok.experimental.UtilityClass;

import java.util.LinkedList;
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
            new EncodingRule(),
            new TrustEventRule(),
            new ValidFrontmatterRule(),
            new ValidLogRule(),
            new ValidSourceRule(),
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

        final List<ValidationIssue> issues = new LinkedList<>();
        knowledgeBundle.bundles()
                // For each bundle =====================================================================================
                .forEach(bundle ->
                        bundle.markdownFiles()
                                // For each markdown file ==============================================================
                                .forEach(markdownFile ->
                                        // With each markdown file rules ===============================================
                                        MARKDOWN_FILE_RULES.forEach(markdownFileRule ->
                                                issues.addAll(markdownFileRule.validate(knowledgeBundle.rootBundle(), markdownFile))
                                        )
                                )
                );
        return ValidationReport.builder()
                .issues(issues)
                .build();
    }

}
