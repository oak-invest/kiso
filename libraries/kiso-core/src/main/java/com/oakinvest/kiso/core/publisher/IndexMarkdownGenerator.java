package com.oakinvest.kiso.core.publisher;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.nio.file.Path;
import java.util.Objects;

import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.CONCEPT;
import static com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX;
import static com.oakinvest.kiso.core.util.FileConstants.ASSETS_DIRECTORY;
import static com.oakinvest.kiso.core.util.MarkdownConstants.HEADING_LEVEL_2;

/**
 * Index markdown (index.md) generator.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class IndexMarkdownGenerator {

    /**
     * Generate the index content for a bundle.
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
                    .forEach(markdownFile -> list.appendChild(markdownFileListItem("", markdownFile)));
            llmsTxt.appendChild(list);
        }

        // Subdirectories ==============================================================================================
        if (!bundle.childBundles().isEmpty()) {
            llmsTxt.appendChild(heading("Subdirectories"));
            BulletList list = new BulletList();
            list.setMarker("-");
            list.setTight(true);

            bundle.childBundles().stream()
                    // Do not take the assets directory at the root !
                    .filter(childBundle -> !Strings.CI.equals(childBundle.relativePath().toString(), ASSETS_DIRECTORY))
                    .forEach(childBundle -> list.appendChild(bundleListItem("", childBundle)));
            llmsTxt.appendChild(list);
        }

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
     * @param baseUrl base url
     * @param bundle  bundle
     * @return list item
     */
    private static ListItem bundleListItem(final String baseUrl, final Bundle bundle) {
        ListItem listItem = new ListItem();
        Paragraph paragraph = new Paragraph();

        // Link + filename.
        Link link = new Link(baseUrl + bundle.simpleName() + "/" + INDEX.getFileName(), null);
        link.appendChild(new Text(bundle.name()));
        paragraph.appendChild(link);

        listItem.appendChild(paragraph);
        return listItem;
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
        Link link = new Link(markdownFile.fileName(), null);
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
        return relativePath.toString().replace('\\', '/');
    }

}
