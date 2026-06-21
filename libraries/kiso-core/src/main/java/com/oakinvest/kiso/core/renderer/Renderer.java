package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * HTML Renderer.
 * - Can be used to toHTML {@link MarkdownFile}.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Renderer {

    /** Markdown parser. */
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();

    /** CommonMark HTML renderer. */
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    /**
     * Render a markdownFile file.
     *
     * @param markdownFile markdownFile file
     * @return content rendered
     */
    public static String toHTML(final MarkdownFile markdownFile) {
        if (markdownFile == null || markdownFile.content() == null) {
            return "";
        }
        Node document = MARKDOWN_PARSER.parse(markdownFile.content());
        return HTML_RENDERER.render(document);
    }

}
