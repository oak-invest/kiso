package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.oakinvest.kiso.core.validation.ValidationCode.DUPLICATE_SOURCE_ID;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Warns when multiple source entries use the same identifier.
 */
public class DuplicateSourceIdRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        final List<ValidationIssue> issues = new LinkedList<>();
        final Set<String> sourceIds = new HashSet<>();
        final Set<String> duplicateSourceIds = new HashSet<>();

        for (Source source : markdownFile.frontmatter().sources()) {
            final String sourceId = source.id();
            if (StringUtils.isBlank(sourceId)) {
                continue;
            }
            if (!sourceIds.add(sourceId) && duplicateSourceIds.add(sourceId)) {
                issues.add(ValidationIssue.builder()
                        .severity(WARNING)
                        .code(DUPLICATE_SOURCE_ID)
                        .message("Multiple sources[] entries use the same id: " + sourceId)
                        .path(markdownFile.relativePath())
                        .build());
            }
        }

        return issues;
    }

}
