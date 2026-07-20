package com.oakinvest.kiso.core.validation;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.rule.EncodingRule;
import com.oakinvest.kiso.core.validation.rule.MarkdownFileRule;
import com.oakinvest.kiso.core.validation.rule.ValidFrontmatterRule;
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
            new EncodingRule(),
            new ValidFrontmatterRule()
    );

    /**
     * Validate a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return validation report;
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
                                                issues.addAll(markdownFileRule.validate(bundle, markdownFile))
                                        )
                                )
                );

        return ValidationReport.builder()
                .issues(issues)
                .build();

    }

}
