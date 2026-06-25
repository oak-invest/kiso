package com.oakinvest.kiso.core.publishing;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.Frontmatter;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;

/**
 * Generator for the sitemap.xml file.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class SitemapXmlGenerator {


    /** Sitemap XML namespace. */
    private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";

    /** Markdown file extension. */
    private static final String MARKDOWN_EXTENSION = ".md";

    /** HTML file extension. */
    private static final String HTML_EXTENSION = ".html";

    /**
     * Generates sitemap.xml content for a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return sitemap.xml content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<urlset xmlns=\"").append(SITEMAP_NAMESPACE).append("\">\n");

        knowledgeBundle.bundles()
                .flatMap(SitemapXmlGenerator::orderedMarkdownFiles)
                .forEach(markdownFile -> appendUrl(content, markdownFile));

        content.append("</urlset>\n");
        return content.toString();
    }

    /**
     * Returns Markdown files with index files first.
     *
     * @param bundle bundle
     * @return ordered Markdown files
     */
    private static Stream<MarkdownFile> orderedMarkdownFiles(final Bundle bundle) {
        return Stream.concat(
                bundle.markdownFiles().stream().filter(markdownFile -> markdownFile.kind().equals(INDEX)),
                bundle.markdownFiles().stream().filter(markdownFile -> !markdownFile.kind().equals(INDEX))
        );
    }

    /**
     * Appends one sitemap URL entry.
     *
     * @param content      sitemap.xml content
     * @param markdownFile Markdown file to append
     */
    private static void appendUrl(final StringBuilder content, final MarkdownFile markdownFile) {
        content.append("  <url>\n");
        content.append("    <loc>")
                .append(escapeXml(htmlPath(markdownFile.relativePath())))
                .append("</loc>\n");

        OffsetDateTime timestamp = timestamp(markdownFile);
        if (timestamp != null) {
            content.append("    <lastmod>")
                    .append(escapeXml(timestamp.toString()))
                    .append("</lastmod>\n");
        }

        content.append("  </url>\n");
    }

    /**
     * Converts a Markdown absolutePath to its generated HTML absolutePath.
     *
     * @param markdownPath Markdown absolutePath relative to the generated site isRoot
     * @return HTML absolutePath
     */
    private static String htmlPath(final Path markdownPath) {
        String path = markdownPath.toString().replace('\\', '/');
        if (path.endsWith(MARKDOWN_EXTENSION)) {
            return path.substring(0, path.length() - MARKDOWN_EXTENSION.length()) + HTML_EXTENSION;
        }
        return path + HTML_EXTENSION;
    }

    /**
     * Returns the frontmatter timestamp of a Markdown file.
     *
     * @param markdownFile Markdown file
     * @return timestamp
     */
    private static OffsetDateTime timestamp(final MarkdownFile markdownFile) {
        Frontmatter frontmatter = markdownFile.frontmatter();
        if (frontmatter == null) {
            return null;
        }
        return frontmatter.timestamp();
    }

    /**
     * Escapes XML control characters.
     *
     * @param text text
     * @return escaped text
     */
    private static String escapeXml(final String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

}
