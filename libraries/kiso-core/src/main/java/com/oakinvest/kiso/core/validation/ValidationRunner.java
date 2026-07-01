package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import com.oakinvest.kiso.core.validation.rule.MarkdownFileRule;

import java.util.LinkedList;
import java.util.List;

/**
 * Validation runner.
 */
public class ValidationRunner {

    /** Markdown file rules. */
    private final List<MarkdownFileRule> markdownFileRules;

    public ValidationRunner() {
        // Markdown file rules.
        this.markdownFileRules = List.of(
                new EncodingRule()
        );
    }

    /**
     * Validate a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return validation report;
     */
    public ValidationReport runValidation(final KnowledgeBundle knowledgeBundle) {
        // TODO If knowledge bundle is null, returns an error.
        List<ValidationIssue> issues = new LinkedList<>();
        knowledgeBundle.bundles()
                // For each bundle =====================================================================================
                .forEach(bundle ->
                        bundle.markdownFiles()
                                // For each markdown file ==============================================================
                                .forEach(markdownFile ->
                                        // With each markdown file rules ===============================================
                                        markdownFileRules.forEach(markdownFileRule ->
                                                issues.addAll(markdownFileRule.validate(bundle, markdownFile))
                                        )
                                )
                );

        return ValidationReport.builder()
                .issues(issues)
                .build();

    }

}
