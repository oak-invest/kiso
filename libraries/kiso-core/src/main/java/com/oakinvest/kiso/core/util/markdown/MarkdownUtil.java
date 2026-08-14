package com.oakinvest.kiso.core.util.markdown;

import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;

import java.nio.file.Path;

/**
 * Markdown utilities for Kiso.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public class MarkdownUtil {

    /**
     * Creates a Markdown file list item.
     *
     * @param markdownFile Markdown file
     * @return list item
     */
    public static ListItem markdownFileListItem(final MarkdownFile markdownFile) {
        return createMarkdownFileListItem(
                markdownFile.fileName(),
                markdownFile
        );
    }

    /**
     * Creates a Markdown file list item.
     *
     * @param baseUrl      public base URL of the generated site
     * @param markdownFile Markdown file
     * @return list item
     */
    public static ListItem markdownFileListItem(
            final String baseUrl,
            final MarkdownFile markdownFile) {

        return createMarkdownFileListItem(
                baseUrl + markdownPath(markdownFile.relativePath()),
                markdownFile
        );
    }

    private static ListItem createMarkdownFileListItem(
            final String url,
            final MarkdownFile markdownFile) {

        ListItem listItem = new ListItem();
        Paragraph paragraph = new Paragraph();

        Link link = new Link(url, null);
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
     * Returns a Markdown path usable in links.
     *
     * @param relativePath Markdown path relative to the knowledge bundle root
     * @return normalized Markdown path
     */
    private static String markdownPath(final Path relativePath) {
        return FilenameUtils.separatorsToUnix(relativePath.toString());
    }

}
