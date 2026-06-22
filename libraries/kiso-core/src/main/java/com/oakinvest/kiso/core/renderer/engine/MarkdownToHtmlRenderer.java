package com.oakinvest.kiso.core.renderer.engine;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.model.markdown.MarkdownFileKind;
import com.oakinvest.kiso.core.renderer.model.ConceptPage;
import com.oakinvest.kiso.core.renderer.util.PageMetadata;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;
import java.util.List;

/**
 * Markdown to HTML Renderer.
 */
public final class MarkdownToHtmlRenderer {

    /** Markdown extensions. */
    private static final List<Extension> MARKDOWN_EXTENSIONS = List.of(AutolinkExtension.create());

    /** Markdown parser. */
    private final Parser markdownParser = Parser.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

    /** CommonMark HTML renderer. */
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

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
                        .path(markdownFile.path().toString())
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
