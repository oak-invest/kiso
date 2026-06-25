package com.oakinvest.kiso.core.rendering.model.navigation;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import lombok.Builder;

import java.nio.file.Path;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.FileExtensions.HTML_EXTENSION;
import static com.oakinvest.kiso.core.util.FileExtensions.MARKDOWN_EXTENSION;

/**
 * A direct Markdown page inside a bundle tree node.
 *
 * @param title        page title displayed in navigation
 * @param fileName     Markdown file name
 * @param relativePath Markdown absolutePath relative to the generated site root
 * @param href         HTML link relative to the generated site root
 * @param kind         semantic kind of the Markdown file
 */
@Builder
@SuppressWarnings("unused")
public record BundleTreePage(
        String title,
        String fileName,
        Path relativePath,
        String href,
        MarkdownFileKind kind
) {

    /**
     * Creates a navigation page from a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return bundle tree page
     */
    public static BundleTreePage fromMarkdownFile(final MarkdownFile markdownFile) {
        Objects.requireNonNull(markdownFile, "markdownFile must not be null");

        return BundleTreePage.builder()
                .title(markdownFile.title())
                .fileName(markdownFile.fileName())
                .relativePath(markdownFile.relativePath())
                .href(toHtmlPath(markdownFile.relativePath()))
                .kind(markdownFile.kind())
                .build();
    }

    /**
     * Converts a Markdown absolutePath to its generated HTML absolutePath.
     *
     * @param markdownPath Markdown absolutePath relative to the generated site isRoot
     * @return HTML absolutePath
     */
    private static String toHtmlPath(final Path markdownPath) {
        String path = markdownPath.toString().replace('\\', '/');
        if (path.endsWith(MARKDOWN_EXTENSION)) {
            return path.substring(0, path.length() - MARKDOWN_EXTENSION.length()) + HTML_EXTENSION;
        }
        return path + HTML_EXTENSION;
    }

}
