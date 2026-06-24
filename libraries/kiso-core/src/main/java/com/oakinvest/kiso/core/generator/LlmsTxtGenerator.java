package com.oakinvest.kiso.core.generator;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.nio.file.Path;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;

/**
 * Generator for the llms.txt file.
 */
public class LlmsTxtGenerator {

    /** Default title used when the knowledge bundle does not provide a readable name. */
    private static final String DEFAULT_TITLE = "Knowledge Bundle";

    /** Default summary used to identify generated Kiso knowledge bundles. */
    private static final String DEFAULT_SUMMARY = "Open Knowledge Format knowledge bundle";

    /** Root section title. */
    private static final String ROOT_SECTION_TITLE = "Root";

    /**
     * Generates llms.txt content for a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return llms.txt content
     */
    public String generate(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        StringBuilder content = new StringBuilder();
        content.append("# ").append(title(knowledgeBundle)).append("\n");
        // content.append("> ").append(DEFAULT_SUMMARY).append("\n");

        knowledgeBundle.bundles()
                .filter(bundle -> !bundle.markdownFiles().isEmpty())
                .forEach(bundle -> appendBundleSection(content, bundle));

        return content.toString();
    }

    /**
     * Appends one bundle section.
     *
     * @param content llms.txt content
     * @param bundle  bundle to append
     */
    private void appendBundleSection(final StringBuilder content, final Bundle bundle) {
        content.append("\n## ").append(sectionTitle(bundle)).append("\n\n");
        bundle.markdownFiles().stream()
                .filter(markdownFile -> markdownFile.kind().equals(INDEX))
                .forEach(markdownFile -> appendMarkdownFile(content, markdownFile));
        bundle.markdownFiles().stream()
                .filter(markdownFile -> !markdownFile.kind().equals(INDEX))
                .forEach(markdownFile -> appendMarkdownFile(content, markdownFile));
    }

    /**
     * Appends one Markdown file link.
     *
     * @param content      llms.txt content
     * @param markdownFile Markdown file to append
     */
    private void appendMarkdownFile(final StringBuilder content, final MarkdownFile markdownFile) {
        content.append("- [")
                .append(escapeLinkText(markdownTitle(markdownFile)))
                .append("](")
                .append(markdownPath(markdownFile.relativePath()))
                .append(")");

        // If it's an index file, we write that the index location as a description.
        String description;
        if (markdownFile.kind().equals(INDEX)) {
            String path = markdownFile.relativePath().toString();
            if (Strings.CI.equals(path, "index.md")) {
                description = "Root index";
            } else {
                description = "Index of " + Strings.CI.replace(path, ".md", "");
            }
        } else {
            description = markdownDescription(markdownFile);
        }

        if (StringUtils.isNotBlank(description)) {
            content.append(": ").append(description);
        }

        content.append("\n");
    }

    /**
     * Returns the llms.txt title.
     *
     * @param knowledgeBundle knowledge bundle
     * @return title
     */
    private String title(final KnowledgeBundle knowledgeBundle) {
        if (StringUtils.isNotBlank(knowledgeBundle.title())) {
            return knowledgeBundle.title();
        }
        Path rootBundlePath = knowledgeBundle.rootBundlePath();
        if (rootBundlePath != null && rootBundlePath.getFileName() != null) {
            return DEFAULT_SUMMARY;
        }
        return DEFAULT_TITLE;
    }

    /**
     * Returns the section title for a bundle.
     *
     * @param bundle bundle
     * @return section title
     */
    private String sectionTitle(final Bundle bundle) {
        if (bundle.relativePath() == null || StringUtils.isBlank(bundle.relativePath().toString())) {
            return ROOT_SECTION_TITLE;
        }
        return bundle.relativePath().toString().replace('\\', '/');
    }

    /**
     * Returns the title for a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return title
     */
    private String markdownTitle(final MarkdownFile markdownFile) {
        Frontmatter frontmatter = markdownFile.frontmatter();
        if (frontmatter != null && StringUtils.isNotBlank(frontmatter.title())) {
            return StringUtils.normalizeSpace(frontmatter.title());
        }
        return markdownFile.fileName();
    }

    /**
     * Returns the description for a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return description
     */
    private String markdownDescription(final MarkdownFile markdownFile) {
        Frontmatter frontmatter = markdownFile.frontmatter();
        if (frontmatter == null || StringUtils.isBlank(frontmatter.description())) {
            return null;
        }
        return StringUtils.normalizeSpace(frontmatter.description());
    }

    /**
     * Returns a Markdown path usable in llms.txt links.
     *
     * @param relativePath Markdown path relative to the knowledge bundle root
     * @return normalized Markdown path
     */
    private String markdownPath(final Path relativePath) {
        return relativePath.toString().replace('\\', '/');
    }

    /**
     * Escapes Markdown link text control characters.
     *
     * @param text link text
     * @return escaped link text
     */
    private String escapeLinkText(final String text) {
        return text.replace("\\", "\\\\")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }

}
