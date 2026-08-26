package com.oakinvest.kiso.cli.publisher;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.util.types.MarkdownFileKind;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.Strings;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.util.Objects;

import static com.oakinvest.kiso.core.tools.MarkdownFactory.markdownFileListItem;
import static com.oakinvest.kiso.core.util.contants.FileConstants.ASSETS_DIRECTORY;
import static com.oakinvest.kiso.core.util.contants.FileConstants.TAGS_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.contants.MarkdownConstants.HEADING_LEVEL_2;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.CONCEPT;

/**
 * Generator for the index.md file of a bundle.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class IndexGenerator {

    /**
     * Generate an index.md file for a bundle.
     *
     * @param bundle bundle
     * @return index.md content
     */
    public static String generate(final Bundle bundle) {
        Objects.requireNonNull(bundle, "bundle must not be null");

        // Document creation ===========================================================================================
        final Document llmsTxt = new Document();

        // Markdown files ==============================================================================================
        if (bundle.hasContent()) {
            llmsTxt.appendChild(heading("Content"));

            BulletList list = new BulletList();
            list.setMarker("-");
            list.setTight(true);

            bundle.markdownFiles()
                    .stream().filter(markdownFile -> markdownFile.kind().equals(CONCEPT))
                    .forEach(markdownFile -> list.appendChild(markdownFileListItem(markdownFile)));
            llmsTxt.appendChild(list);
        }

        // Subdirectories ==============================================================================================
        if (!bundle.childBundles().isEmpty()) {
            llmsTxt.appendChild(heading("Subdirectories"));
            BulletList list = new BulletList();
            list.setMarker("-");
            list.setTight(true);

            bundle.childBundles().stream()
                    // Do not add tags.
                    .filter(childBundle -> !childBundle.name().equalsIgnoreCase(TAGS_DIRECTORY_NAME))
                    // Do not take the assets directory at the root!
                    .filter(childBundle -> !Strings.CI.equals(childBundle.relativePath().toString(), ASSETS_DIRECTORY))
                    .forEach(childBundle -> list.appendChild(bundleListItem(childBundle)));
            llmsTxt.appendChild(list);
        }

        // Return generated file =======================================================================================
        return MarkdownRenderer.builder()
                .build()
                .render(llmsTxt);
    }

    /**
     * Creates a heading.
     *
     * @param text heading text
     * @return heading
     */
    private static Heading heading(final String text) {
        Heading heading = new Heading();
        heading.setLevel(HEADING_LEVEL_2);
        heading.appendChild(new Text(text));
        return heading;
    }

    /**
     * Creates a bundle list item.
     *
     * @param bundle bundle
     * @return list item
     */
    private static ListItem bundleListItem(final Bundle bundle) {
        ListItem listItem = new ListItem();
        Paragraph paragraph = new Paragraph();

        // Link + filename.
        Link link = new Link(bundle.simpleName() + "/" + MarkdownFileKind.INDEX.getFileName(), null);
        link.appendChild(new Text(bundle.name()));
        paragraph.appendChild(link);

        listItem.appendChild(paragraph);
        return listItem;
    }

}
