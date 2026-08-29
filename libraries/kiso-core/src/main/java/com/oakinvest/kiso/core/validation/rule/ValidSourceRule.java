package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_SOURCE_LAST_MODIFIED;
import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_SOURCE_RESOURCE;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Sources rule.
 * For every source entry:
 * - Require sources[].resource.
 * - Validate sources[].last_modified as YYYY-MM-DD when present.
 */
public class ValidSourceRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        final List<ValidationIssue> issues = new LinkedList<>();

        for (Source source : markdownFile.frontmatter().sources()) {
            if (StringUtils.isBlank(source.resource())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(MISSING_SOURCE_RESOURCE)
                        .message("Missing sources[].resource field in frontmatter")
                        .path(markdownFile.relativePath())
                        .build());
            }

            if (StringUtils.isNotBlank(source.lastModified())
                    && !isValidISO8601LocalDate(source.lastModified())) {
                issues.add(ValidationIssue.builder()
                        .severity(ERROR)
                        .code(INVALID_SOURCE_LAST_MODIFIED)
                        .message("Invalid sources[].last_modified field in frontmatter - Not an ISO 8601 date")
                        .path(markdownFile.relativePath())
                        .build());
            }
        }

        return issues;
    }

}
