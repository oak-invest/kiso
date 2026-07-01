package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.MarkdownConstants.HEADING_LEVEL_1;
import static com.oakinvest.kiso.core.util.MarkdownConstants.HEADING_LEVEL_2;
import static com.oakinvest.kiso.core.util.OKFConstants.DEFAULT_TITLE;

/**
 * Generator for the llms.txt file.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class LlmsTxtGenerator {

    /**
     * Generates llms.txt content for a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return llms.txt content
     */
    public static String generate(final KnowledgeBundle knowledgeBundle) {
        Objects.requireNonNull(knowledgeBundle, "knowledgeBundle must not be null");

        // Document creation ===========================================================================================
        final Document llmsTxt = new Document();
        llmsTxt.appendChild(heading(HEADING_LEVEL_1, DEFAULT_TITLE));

        // Each bundle section =========================================================================================
        knowledgeBundle.bundles().forEach(bundle -> {

            // Bundle name =============================================================================================
            llmsTxt.appendChild(heading(HEADING_LEVEL_2, bundle.name()));

            // Pages inside the bundle =================================================================================
            BulletList list = new BulletList();
            list.setMarker("-");
            list.setTight(true);
            bundle.markdownFiles().stream()
                    .sorted(Comparator.comparing(markdownFile -> markdownFile.kind() != INDEX))
                    .forEach(markdownFile -> list.appendChild(markdownFileListItem(markdownFile)));
            llmsTxt.appendChild(list);
        });

        return MarkdownRenderer.builder()
                .build()
                .render(llmsTxt);
    }

    /**
     * Creates a heading.
     *
     * @param level heading level
     * @param text  heading text
     * @return heading
     */
    private static Heading heading(final int level, final String text) {
        Heading heading = new Heading();
        heading.setLevel(level);
        heading.appendChild(new Text(text));
        return heading;
    }

    /**
     * Creates one Markdown file list item.
     *
     * @param markdownFile Markdown file
     * @return list item
     */
    private static ListItem markdownFileListItem(final MarkdownFile markdownFile) {
        ListItem listItem = new ListItem();
        Paragraph paragraph = new Paragraph();
        Link link = new Link(markdownPath(markdownFile.relativePath()), null);
        link.appendChild(new Text(markdownFile.title()));
        paragraph.appendChild(link);

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
        return relativePath.toString().replace('\\', '/');
    }

}
