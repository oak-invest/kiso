package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.model.bundle.Bundle;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import lombok.experimental.UtilityClass;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * HTML Renderer.
 * - Can be used to toHTML {@link KnowledgeBundle}.
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
     * Render a knowledge bundle.
     *
     * @param knowledgeBundle knowledge bundle
     * @return content rendered
     */
    public static String toHTML(final KnowledgeBundle knowledgeBundle) {
        if (knowledgeBundle == null || knowledgeBundle.rootBundle() == null) {
            return "";
        }
        return toHTML(knowledgeBundle.rootBundle());
    }

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

    /**
     * Render a bundle directory.
     *
     * @param bundle bundle directory
     * @return content rendered
     */
    private static String toHTML(final Bundle bundle) {
        StringBuilder html = new StringBuilder();
        bundle.markdownFiles().forEach(markdownFile -> html.append(toHTML(markdownFile)));
        bundle.childBundleDirectories().forEach(childBundle -> html.append(toHTML(childBundle)));
        return html.toString();
    }

}
