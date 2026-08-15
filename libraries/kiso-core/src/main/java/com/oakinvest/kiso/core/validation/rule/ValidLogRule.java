package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.oakinvest.kiso.core.util.contants.MarkdownConstants.HEADING_LEVEL_2;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.LOG;
import static com.oakinvest.kiso.core.validation.ValidationCode.INVALID_LOG_DATE_FORMAT;
import static com.oakinvest.kiso.core.validation.ValidationSeverity.ERROR;

/**
 * Valid log rule.
 */
public class ValidLogRule implements MarkdownFileRule {

    @Override
    public final List<ValidationIssue> validate(final Bundle bundle, final MarkdownFile markdownFile) {
        final List<ValidationIssue> issues = new LinkedList<>();

        // We treat only log files in this rule.
        if (markdownFile.kind().equals(LOG)) {

            // Validate log.md date headings use the YYYY-MM-DD format =================================================
            for (String heading : extractLevelTwoHeadings(markdownFile.body())) {
                // Validation logic for each heading
                if (!isValidISO8601LocalDate(StringUtils.trim(heading))) {
                    issues.add(ValidationIssue.builder()
                            .severity(ERROR)
                            .code(INVALID_LOG_DATE_FORMAT)
                            .message("Invalid heading date format - Not an ISO 8601 date: " + heading)
                            .path(markdownFile.relativePath())
                            .build());
                }
            }

        }
        return issues;
    }

    /**
     * Extracts level two headings from the given Markdown content.
     *
     * @param content the Markdown content
     * @return a list of level two heading texts
     */
    private List<String> extractLevelTwoHeadings(final String content) {
        final Parser parser = Parser.builder().build();
        final Node document = parser.parse(content);
        final List<String> headings = new ArrayList<>();

        document.accept(new AbstractVisitor() {
            @Override
            public void visit(final Heading heading) {
                if (heading.getLevel() == HEADING_LEVEL_2 && heading.getFirstChild() instanceof Text text) {
                    headings.add(text.getLiteral());
                }
                visitChildren(heading);
            }
        });

        return headings;
    }

}
