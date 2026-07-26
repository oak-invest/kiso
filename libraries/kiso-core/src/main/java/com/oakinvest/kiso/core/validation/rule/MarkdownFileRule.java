package com.oakinvest.kiso.core.validation.rule;

import com.oakinvest.kiso.core.model.okf.bundle.Bundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.validation.ValidationIssue;

import java.util.List;

/**
 * Markdown file rule.
 */
public interface MarkdownFileRule {

    /**
     * Returns validation issues of a markdown file.
     *
     * @param bundle       directory - Bundle
     * @param markdownFile file - Markdown file
     * @return list of issues
     */
    List<ValidationIssue> validate(Bundle bundle, MarkdownFile markdownFile);

}
