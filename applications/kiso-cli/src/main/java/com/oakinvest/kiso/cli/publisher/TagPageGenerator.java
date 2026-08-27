package com.oakinvest.kiso.cli.publisher;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.util.Objects;

import static com.oakinvest.kiso.core.tools.MarkdownFactory.markdownFileListItem;

/**
 * Generator for the tag pages of a bundle.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class TagPageGenerator {

    /**
     * Generate a tag page for a bundle.
     *
     * @param knowledgeBundle bundle where the pages are
     * @param tagSlug         tag normalize for which the page is generated
     * @return tag page content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle, final String tagSlug) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(tagSlug, "tagSlug must not be null");

        // Define base url =============================================================================================
        final String baseUrl;
        if (StringUtils.isBlank(knowledgeBundle.siteConfiguration().normalizedBaseUrl())) {
            // Because the tag page is generated in a subfolder, we need to go one level up to reach the root of the site.
            baseUrl = "../";
        } else {
            baseUrl = knowledgeBundle.siteConfiguration().normalizedBaseUrl();
        }

        // Document creation ===========================================================================================
        final Document tagPage = new Document();
        knowledgeBundle.markdownFiles()
                .filter(MarkdownFile::frontmatterPresent)
                .filter(markdownFile -> markdownFile.frontmatter().tagSlugs().contains(tagSlug))
                .forEach(markdownFile -> {
                    BulletList list = new BulletList();
                    list.setMarker("-");
                    list.setTight(true);
                    list.appendChild(markdownFileListItem(baseUrl, markdownFile));
                    tagPage.appendChild(list);
                });

        // Return frontmatter and document content =====================================================================
        return """
                ---
                type: tag
                title: %s
                ---
                
                %s""".formatted(tagSlug, MarkdownRenderer.builder().build().render(tagPage));
    }

}
