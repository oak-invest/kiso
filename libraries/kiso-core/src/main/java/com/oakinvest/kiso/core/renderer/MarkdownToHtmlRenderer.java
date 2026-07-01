package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.model.markdown.MarkdownFile;
import com.oakinvest.kiso.core.renderer.model.PageMetadata;
import com.oakinvest.kiso.core.renderer.model.navigation.BundleTree;
import com.oakinvest.kiso.core.renderer.model.page.ConceptPage;
import com.oakinvest.kiso.core.renderer.model.page.IndexPage;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import lombok.experimental.UtilityClass;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Markdown to HTML Renderer.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class MarkdownToHtmlRenderer {

    /** Markdown extensions. */
    private static final List<Extension> MARKDOWN_EXTENSIONS = List.of(AutolinkExtension.create());

    /** Markdown parser. */
    private static final Parser MARKDOWN_PARSER = Parser.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

    /** CommonMark HTML renderer. */
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

    /** JTE precompiled index template class. */
    private static final String PRECOMPILED_INDEX_TEMPLATE_CLASS = "gg.jte.generated.precompiled.JteindexGenerated";

    /** JTE source templates directory when tests are launched directly from the repository isRoot. */
    private static final Path ROOT_SOURCE_TEMPLATES_DIRECTORY = Path.of("libraries/kiso-core/src/main/jte");

    /** JTE source templates directory when tests are launched directly from the module directory. */
    private static final Path MODULE_SOURCE_TEMPLATES_DIRECTORY = Path.of("src/main/jte");

    /** JTE - Java Template engine. */
    private static final TemplateEngine TEMPLATE_ENGINE = createTemplateEngine();

    /**
     * Creates the JTE template engine.
     *
     * @return JTE template engine
     */
    private static TemplateEngine createTemplateEngine() {
        if (precompiledTemplatesAvailable()) {
            return TemplateEngine.createPrecompiled(ContentType.Html);
        }

        Path sourceTemplatesDirectory = sourceTemplatesDirectory();
        if (sourceTemplatesDirectory != null) {
            return TemplateEngine.create(new DirectoryCodeResolver(sourceTemplatesDirectory), ContentType.Html);
        }

        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    /**
     * Returns true when JTE precompiled templates are available on the classpath.
     *
     * @return true when precompiled templates are available
     */
    private static boolean precompiledTemplatesAvailable() {
        try {
            Class.forName(PRECOMPILED_INDEX_TEMPLATE_CLASS);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    /**
     * Returns the source templates directory when available.
     *
     * @return source templates directory, or null
     */
    private static Path sourceTemplatesDirectory() {
        if (Files.isDirectory(ROOT_SOURCE_TEMPLATES_DIRECTORY)) {
            return ROOT_SOURCE_TEMPLATES_DIRECTORY;
        }
        if (Files.isDirectory(MODULE_SOURCE_TEMPLATES_DIRECTORY)) {
            return MODULE_SOURCE_TEMPLATES_DIRECTORY;
        }
        return null;
    }

    /**
     * Render a markdownFile file to HTML.
     *
     * @param markdownFile markdownFile file
     * @param bundleTree   calculated bundle tree for navigation
     * @return content rendered
     */
    public static String render(final MarkdownFile markdownFile, final BundleTree bundleTree) {
        if (markdownFile == null || markdownFile.content() == null) {
            return "";
        }

        // Generating the HTML =========================================================================================
        final Node document = MARKDOWN_PARSER.parse(markdownFile.content());
        String htmlContent = HTML_RENDERER.render(document).replaceAll(
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
                                .absolutePath(markdownFile.absolutePath().toString())
                                .assetBasePath(assetBasePath(markdownFile.relativePath()))
                                .htmlPath(markdownFile.htmlFilePath())
                                .build())
                        .bundleTree(bundleTree)
                        .htmlContent(output -> output.writeContent(htmlContent))
                        .build();

                StringOutput output = new StringOutput();
                TEMPLATE_ENGINE.render("index.jte", page, output);
                return output.toString();
            }
            default -> {
                // Concept =============================================================================================
                ConceptPage page = ConceptPage.builder()
                        .metadata(PageMetadata.builder()
                                .title(markdownFile.frontmatter().title())
                                .description(markdownFile.frontmatter().description())
                                .absolutePath(markdownFile.absolutePath().toString())
                                .assetBasePath(assetBasePath(markdownFile.relativePath()))
                                .htmlPath(markdownFile.htmlFilePath())
                                .build())
                        .type(markdownFile.frontmatter().type())
                        .resource(markdownFile.frontmatter().resource())
                        .tags(markdownFile.frontmatter().tags())
                        .timestamp(markdownFile.frontmatter().timestamp())
                        .bundleTree(bundleTree)
                        .htmlContent(output -> output.writeContent(htmlContent))
                        .build();

                StringOutput output = new StringOutput();
                TEMPLATE_ENGINE.render("concept.jte", page, output);
                return output.toString();
            }
        }
    }

    /**
     * Returns the relative absolutePath from an HTML page to the generated site root.
     *
     * @param markdownRelativePath Markdown absolutePath relative to the site root
     * @return asset base absolute base path
     */
    private static String assetBasePath(final Path markdownRelativePath) {
        if (markdownRelativePath == null || markdownRelativePath.getParent() == null) {
            return "";
        }
        return "../".repeat(markdownRelativePath.getParent().getNameCount());
    }

}
