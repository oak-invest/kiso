package com.oakinvest.kiso.core.renderer.engine;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import com.oakinvest.kiso.core.renderer.model.ConceptPage;
import com.oakinvest.kiso.core.renderer.util.PageMetadata;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;

/**
 * Markdown to HTML Renderer.
 */
public final class MarkdownToHtmlRenderer {

    /** Markdown parser. */
    private final Parser markdownParser = Parser.builder().build();

    /** CommonMark HTML renderer. */
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    /** JTE - Java Template engine. */
    private TemplateEngine templateEngine;

    /**
     * Constructor.
     */
    public MarkdownToHtmlRenderer() {
        DirectoryCodeResolver resolver = new DirectoryCodeResolver(Path.of("src/main/jte"));
        templateEngine = TemplateEngine.create(resolver, ContentType.Html);
    }

    /**
     * Render a markdownFile file to HTML.
     *
     * @param markdownFile markdownFile file
     * @return content rendered
     */
    public String render(final MarkdownFile markdownFile) {
        if (markdownFile == null || markdownFile.content() == null) {
            return "";
        }
        final Node document = markdownParser.parse(markdownFile.content());

        ConceptPage page = ConceptPage.builder()
                .metadata(PageMetadata.builder()
                        .title(markdownFile.frontmatter().title())
                        .description(markdownFile.frontmatter().description())
                        .build())
                .type(markdownFile.kind().name())
                .resource(markdownFile.frontmatter().resource())
                .tags(markdownFile.frontmatter().tags())
                .timestamp(markdownFile.frontmatter().timestamp())
                .htmlContent(output -> output.writeContent(htmlRenderer.render(document)))
                .build();

        if (markdownFile.kind().equals(MarkdownFileKind.CONCEPT)) {
            StringOutput output = new StringOutput();
            templateEngine.render("concept.jte", page, output);
            return output.toString();
        }
        return htmlRenderer.render(document);
    }

}
