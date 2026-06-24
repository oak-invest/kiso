package com.oakinvest.kiso.core.renderer.engine;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.renderer.model.ConceptPage;
import com.oakinvest.kiso.core.renderer.model.IndexPage;
import com.oakinvest.kiso.core.renderer.model.PackageTree;
import com.oakinvest.kiso.core.renderer.util.PageMetadata;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
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
    private final TemplateEngine templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);

    /**
     * Render a markdownFile file to HTML.
     *
     * @param markdownFile markdownFile file
     * @return content rendered
     */
    public String render(final MarkdownFile markdownFile) {
        return render(markdownFile, null);
    }

    /**
     * Render a markdownFile file to HTML.
     *
     * @param markdownFile markdownFile file
     * @param packageTree  calculated package tree for navigation
     * @return content rendered
     */
    public String render(final MarkdownFile markdownFile, final PackageTree packageTree) {
        if (markdownFile == null || markdownFile.content() == null) {
            return "";
        }

        // Generating the HTML =========================================================================================
        final Node document = markdownParser.parse(markdownFile.content());
        String html = htmlRenderer.render(document).replaceAll(
                "href=\"(?!https?://|mailto:|#)([^\"]+)\\.md\"",
                "href=\"$1.html\""
        );

        // Choose and render depending on the kind =====================================================================
        switch (markdownFile.kind()) {
            case LOG -> {
                // Log - No treatment, just return the HTML content.
                return "";
            }
            case INDEX -> {
                // Index ===============================================================================================
                IndexPage page = IndexPage.builder()
                        .metadata(PageMetadata.builder()
                                .title(markdownFile.relativePath().toString())
                                .path(markdownFile.path().toString())
                                .assetBasePath(assetBasePath(markdownFile.relativePath()))
                                .htmlPath(htmlPath(markdownFile.relativePath()))
                                .build())
                        .packageTree(packageTree)
                        .htmlContent(output -> output.writeContent(html))
                        .build();

                StringOutput output = new StringOutput();
                templateEngine.render("index.jte", page, output);
                return output.toString();
            }
            default -> {
                // Concept =============================================================================================
                ConceptPage page = ConceptPage.builder()
                        .metadata(PageMetadata.builder()
                                .title(markdownFile.frontmatter().title())
                                .description(markdownFile.frontmatter().description())
                                .path(markdownFile.path().toString())
                                .assetBasePath(assetBasePath(markdownFile.relativePath()))
                                .htmlPath(htmlPath(markdownFile.relativePath()))
                                .build())
                        .type(markdownFile.frontmatter().type())
                        .resource(markdownFile.frontmatter().resource())
                        .tags(markdownFile.frontmatter().tags())
                        .timestamp(markdownFile.frontmatter().timestamp())
                        .packageTree(packageTree)
                        .htmlContent(output -> output.writeContent(html))
                        .build();

                StringOutput output = new StringOutput();
                templateEngine.render("concept.jte", page, output);
                return output.toString();
            }
        }
    }

    /**
     * Returns the generated HTML path relative to the generated site root.
     *
     * @param markdownRelativePath Markdown path relative to the site root
     * @return HTML path
     */
    private String htmlPath(final Path markdownRelativePath) {
        if (markdownRelativePath == null) {
            return null;
        }
        String path = markdownRelativePath.toString().replace('\\', '/');
        if (path.endsWith(".md")) {
            return path.substring(0, path.length() - ".md".length()) + ".html";
        }
        return path + ".html";
    }

    /**
     * Returns the relative path from an HTML page to the generated site root.
     *
     * @param markdownRelativePath Markdown path relative to the site root
     * @return asset base path
     */
    private String assetBasePath(final Path markdownRelativePath) {
        if (markdownRelativePath == null || markdownRelativePath.getParent() == null) {
            return "";
        }
        return "../".repeat(markdownRelativePath.getParent().getNameCount());
    }

}
