package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.FileConstants.ASSETS_DIRECTORY;
import static com.oakinvest.kiso.core.util.SitemapConstants.SITEMAP_NAMESPACE;

/**
 * Generator for the sitemap.xml file.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class SitemapXmlGenerator {

    /**
     * Generates sitemap.xml content for a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return sitemap.xml content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        // Building the sitemap ========================================================================================
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<urlset xmlns=\"").append(SITEMAP_NAMESPACE).append("\">\n");
        knowledgeBundle.bundles()
                .filter(bundle -> {
                    Path path = bundle.relativePath();
                    return path.getNameCount() == 0 || !path.getName(0).toString().equals(ASSETS_DIRECTORY);
                })
                .forEach(bundle -> bundle.markdownFiles().stream()
                        // Index first.
                        .sorted(Comparator.comparing(markdownFile -> markdownFile.kind() != INDEX))
                        .forEach(markdownFile -> appendUrl(content, knowledgeBundle.siteConfiguration().normalizedBaseUrl(), markdownFile)));
        content.append("</urlset>\n");
        return content.toString();
    }

    /**
     * Appends one sitemap URL entry.
     *
     * @param content      sitemap.xml content
     * @param baseUrl      public base URL of the generated site
     * @param markdownFile Markdown file to append
     */
    private static void appendUrl(final StringBuilder content, final String baseUrl, final MarkdownFile markdownFile) {
        content.append("<url>\n");

        // Location ====================================================================================================
        content.append("<loc>")
                .append(StringEscapeUtils.escapeXml11(Objects.requireNonNullElse(baseUrl, "")))
                .append(StringEscapeUtils.unescapeXml(markdownFile.htmlFilePath()))
                .append("</loc>\n");

        // Timestamp ===================================================================================================
        OffsetDateTime timestamp = markdownFile.timestamp();
        if (timestamp != null) {
            content.append("<lastmod>")
                    .append(StringEscapeUtils.unescapeXml(timestamp.toString()))
                    .append("</lastmod>\n");
        }

        content.append("</url>\n");
    }

}
