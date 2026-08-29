package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.provenance.Source;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.oakinvest.kiso.core.validation.ValidationCode.MISSING_SOURCE_FOR_FOOTNOTE;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.WARNING;

/**
 * Warn when a body footnote does not match a source identifier.
 */
public class SourceFootnoteRule implements MarkdownFileRule {

    /** Markdown footnote label pattern. */
    private static final Pattern FOOTNOTE_PATTERN = Pattern.compile("\\[\\^([^]\\r\\n]+)]");

    @Override
    public final List<ValidationIssue> validate(final KnowledgeBundle knowledgeBundle, final MarkdownFile markdownFile) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        final Set<String> sourceIdentifiers = markdownFile.frontmatter().sources().stream()
                .map(Source::id)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        return footnoteIdentifiers(markdownFile.body()).stream()
                .filter(identifier -> !sourceIdentifiers.contains(identifier))
                .map(identifier -> missingSourceIssue(markdownFile, identifier))
                .toList();
    }

    private Set<String> footnoteIdentifiers(final String body) {
        if (StringUtils.isBlank(body)) {
            return Set.of();
        }

        final Set<String> identifiers = new LinkedHashSet<>();
        final Matcher matcher = FOOTNOTE_PATTERN.matcher(body);
        while (matcher.find()) {
            identifiers.add(matcher.group(1));
        }
        return identifiers;
    }

    private ValidationIssue missingSourceIssue(final MarkdownFile markdownFile, final String identifier) {
        return ValidationIssue.builder()
                .severity(WARNING)
                .code(MISSING_SOURCE_FOR_FOOTNOTE)
                .message("Footnote [^" + identifier + "] does not match any sources[].id")
                .path(markdownFile.relativePath())
                .build();
    }

}
