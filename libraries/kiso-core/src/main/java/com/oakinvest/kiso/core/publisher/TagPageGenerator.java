package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.model.okf.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.nio.file.Path;
import java.util.Objects;

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
     * @param tag             tag for which the page is generated
     * @return tag page content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle, final String tag) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");
        Objects.requireNonNull(tag, "tag must not be null");

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
                .filter(markdownFile -> markdownFile.frontmatter().tags().contains(tag))
                .forEach(markdownFile -> {
                    BulletList list = new BulletList();
                    list.setMarker("-");
                    list.setTight(true);
                    list.appendChild(markdownFileListItem(
                            baseUrl,
                            markdownFile));
                    tagPage.appendChild(list);
                });

        // Return frontmatter and document content =====================================================================
        return """
                ---
                type: tag
                title: Tag %s
                ---
                
                %s""".formatted(tag, MarkdownRenderer.builder().build().render(tagPage));
    }

    /**
     * Creates one Markdown file list item.
     *
     * @param baseUrl      public base URL of the generated site
     * @param markdownFile Markdown file
     * @return list item
     */
    private static ListItem markdownFileListItem(final String baseUrl, final MarkdownFile markdownFile) {
        ListItem listItem = new ListItem();
        Paragraph paragraph = new Paragraph();

        // Link + filename.
        Link link = new Link(baseUrl + markdownPath(markdownFile.relativePath()), null);
        link.appendChild(new Text(markdownFile.title()));
        paragraph.appendChild(link);

        // Description.
        String description = markdownFile.description();
        if (StringUtils.isNotBlank(description)) {
            paragraph.appendChild(new Text(": " + description));
        }

        listItem.appendChild(paragraph);
        return listItem;
    }

    /**
     * Returns a Markdown path usable in llms.txt links.
     *
     * @param relativePath Markdown path relative to the knowledge bundle root
     * @return normalized Markdown path
     */
    private static String markdownPath(final Path relativePath) {
        return FilenameUtils.separatorsToUnix(relativePath.toString());
    }

}
