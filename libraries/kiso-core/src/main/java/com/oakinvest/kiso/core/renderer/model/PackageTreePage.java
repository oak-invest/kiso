package com.oakinvest.kiso.core.renderer.model;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import lombok.Builder;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A direct Markdown page inside a package tree node.
 *
 * @param title        page title displayed in navigation
 * @param fileName     Markdown file name
 * @param relativePath Markdown path relative to the generated site root
 * @param href         HTML link relative to the generated site root
 * @param kind         semantic kind of the Markdown file
 */
@Builder
@SuppressWarnings("unused")
public record PackageTreePage(
        String title,
        String fileName,
        Path relativePath,
        String href,
        MarkdownFileKind kind
) {

    /** Markdown file extension. */
    private static final String MARKDOWN_EXTENSION = ".md";

    /** HTML file extension. */
    private static final String HTML_EXTENSION = ".html";

    /**
     * Creates a navigation page from a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return package tree page
     */
    public static PackageTreePage fromMarkdownFile(final MarkdownFile markdownFile) {
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        return PackageTreePage.builder()
                .title(pageTitle(markdownFile))
                .fileName(markdownFile.fileName())
                .relativePath(markdownFile.relativePath())
                .href(toHtmlPath(markdownFile.relativePath()))
                .kind(markdownFile.kind())
                .build();
    }

    /**
     * Returns a readable page title.
     *
     * @param markdownFile Markdown file
     * @return page title
     */
    private static String pageTitle(final MarkdownFile markdownFile) {
        if (markdownFile.frontmatter() != null && markdownFile.frontmatter().title() != null) {
            return markdownFile.frontmatter().title();
        }
        return markdownFile.fileName();
    }

    /**
     * Converts a Markdown path to its generated HTML path.
     *
     * @param markdownPath Markdown path relative to the generated site root
     * @return HTML path
     */
    private static String toHtmlPath(final Path markdownPath) {
        String path = markdownPath.toString().replace('\\', '/');
        if (path.endsWith(MARKDOWN_EXTENSION)) {
            return path.substring(0, path.length() - MARKDOWN_EXTENSION.length()) + HTML_EXTENSION;
        }
        return path + HTML_EXTENSION;
    }

}
