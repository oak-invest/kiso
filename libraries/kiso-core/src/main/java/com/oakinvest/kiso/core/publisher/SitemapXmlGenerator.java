package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.text.StringEscapeUtils;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;

import static com.oakinvest.kiso.core.util.contants.FileConstants.TAGS_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * Generator for the sitemap.xml file.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "HttpUrlsUsage"})
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
        content.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                """);
        knowledgeBundle.bundles()
                .filter(bundle -> !bundle.name().equalsIgnoreCase(TAGS_DIRECTORY_NAME))
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
