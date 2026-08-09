package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.configuration.SiteConfiguration;
import com.oakinvest.kiso.core.configuration.ThemeConfiguration;
import com.oakinvest.kiso.core.model.html.navigation.BundleTree;
import com.oakinvest.kiso.core.model.html.page.ConceptPage;
import com.oakinvest.kiso.core.model.html.page.IndexPage;
import com.oakinvest.kiso.core.model.html.util.PageContext;
import com.oakinvest.kiso.core.model.html.util.PageMetadata;
import com.oakinvest.kiso.core.model.okf.markdown.MarkdownFile;
import com.oakinvest.kiso.core.renderer.util.HtmlCleaner;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Strings;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.util.TemplateConstants.CONCEPT_TEMPLATE_PAGE;
import static com.oakinvest.kiso.core.util.TemplateConstants.INDEX_TEMPLATE_PAGE;
import static com.oakinvest.kiso.core.util.TemplateConstants.MODULE_SOURCE_TEMPLATES_DIRECTORY;
import static com.oakinvest.kiso.core.util.TemplateConstants.PRECOMPILED_INDEX_TEMPLATE_CLASS;
import static com.oakinvest.kiso.core.util.TemplateConstants.ROOT_SOURCE_TEMPLATES_DIRECTORY;

/**
 * Markdown to HTML Renderer.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class MarkdownToHtmlRenderer {

    /** Markdown extensions. */
    private static final List<Extension> MARKDOWN_EXTENSIONS = List.of(
            AutolinkExtension.create(),
            TablesExtension.create()
    );

    /** Markdown parser. */
    private static final Parser MARKDOWN_PARSER = Parser.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

    /** CommonMark HTML renderer. */
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder()
            .extensions(MARKDOWN_EXTENSIONS)
            .build();

    /** JTE - Java Template engine. */
    private static final TemplateEngine TEMPLATE_ENGINE = createTemplateEngine();

    /** HTML cleaner. */
    private static final HtmlCleaner HTML_CLEANER = new HtmlCleaner();

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
     * @return {@code true} when precompiled templates are available
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
     * @param siteConfiguration  site configuration
     * @param themeConfiguration theme configuration
     * @param markdownFile       markdownFile file
     * @param bundleTree         calculated bundle tree for navigation
     * @return content rendered
     */
    public static String render(final SiteConfiguration siteConfiguration,
                                final ThemeConfiguration themeConfiguration,
                                final MarkdownFile markdownFile,
                                final BundleTree bundleTree) {
        if (markdownFile == null || markdownFile.body() == null) {
            return "";
        }

        // Generating the HTML =========================================================================================
        final Node document = MARKDOWN_PARSER.parse(markdownFile.body());

        // Transforming .md file links to .html links ==================================================================
        // Matches HTML href attributes that reference a local Markdown file (.md), while excluding absolute HTTP/HTTPS
        // URLs, mailto: links, and anchor (#) links.
        String htmlContent = HTML_RENDERER.render(document).replaceAll(
                "href=\"(?!https?://|mailto:|#)([^\"]+)\\.md\"",
                "href=\"$1.html\""
        );

        // Choose and render depending on the kind =====================================================================
        StringOutput htmlOutput = new StringOutput();
        switch (markdownFile.kind()) {
            case LOG -> {
                // Log - No treatment for now, return nothing ==========================================================
                return "";
            }
            case INDEX -> {
                // Index ===============================================================================================
                IndexPage page = IndexPage.builder()
                        .context(PageContext.builder()
                                .siteConfiguration(siteConfiguration)
                                .themeConfiguration(themeConfiguration)
                                .bundleTree(bundleTree)
                                .build())
                        .metadata(PageMetadata.builder()
                                .title(ObjectUtils.firstNonNull(siteConfiguration.title(), markdownFile.relativePath().toString()))
                                .description(siteConfiguration.description())
                                .absolutePath(markdownFile.absolutePath().toString())
                                .assetBasePath(assetBasePath(siteConfiguration, markdownFile.relativePath()))
                                .htmlPath(markdownFile.htmlFilePath())
                                .build())
                        .htmlContent(output -> output.writeContent(
                                // For index file generated by Kiso, we add i18n properties.
                                Strings.CI.replace(htmlContent, "<h2>Content</h2>", "<h2 data-i18n=\"content.content\">Content</h2>")
                                        .replace("<h2>Subdirectories</h2>", "<h2 data-i18n=\"content.subdirectories\">Subdirectories</h2>"))
                        )
                        .build();

                TEMPLATE_ENGINE.render(INDEX_TEMPLATE_PAGE, page, htmlOutput);
            }
            default -> {
                // Concept =============================================================================================
                ConceptPage page = ConceptPage.builder()
                        .context(PageContext.builder()
                                .siteConfiguration(siteConfiguration)
                                .themeConfiguration(themeConfiguration)
                                .bundleTree(bundleTree)
                                .build())
                        .metadata(PageMetadata.builder()
                                .title(markdownFile.title())
                                .description(markdownFile.description())
                                .absolutePath(markdownFile.absolutePath().toString())
                                .assetBasePath(assetBasePath(siteConfiguration, markdownFile.relativePath()))
                                .htmlPath(markdownFile.htmlFilePath())
                                .build())
                        .type(markdownFile.frontmatter().type())
                        .resource(markdownFile.frontmatter().resource())
                        .tags(markdownFile.frontmatter().tags())
                        .generatedBy(markdownFile.frontmatter().generatedBy())
                        .generatedAt(markdownFile.frontmatter().generatedAt())
                        .timestamp(markdownFile.timestamp())
                        .htmlContent(output -> output.writeContent(htmlContent))
                        .build();

                TEMPLATE_ENGINE.render(CONCEPT_TEMPLATE_PAGE, page, htmlOutput);
            }
        }
        return HTML_CLEANER.clean(htmlOutput.toString());
    }

    /**
     * Returns the relative absolutePath from an HTML page to the generated site root.
     *
     * @param siteConfiguration    site configuration
     * @param markdownRelativePath Markdown absolutePath relative to the site root
     * @return asset base absolute base path
     */
    private static String assetBasePath(final SiteConfiguration siteConfiguration, final Path markdownRelativePath) {
        if (!siteConfiguration.normalizedBaseUrl().isEmpty()) {
            return siteConfiguration.normalizedBaseUrl();
        }
        if (markdownRelativePath == null || markdownRelativePath.getParent() == null) {
            return "";
        }
        return "../" .repeat(markdownRelativePath.getParent().getNameCount());
    }

}
