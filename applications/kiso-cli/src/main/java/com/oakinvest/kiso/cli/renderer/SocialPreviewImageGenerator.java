package com.oakinvest.kiso.cli.renderer;

import com.oakinvest.kiso.cli.exception.SocialPreviewGenerationException;
import com.oakinvest.kiso.cli.model.image.SocialPreviewImage;
import com.oakinvest.kiso.cli.tool.SvgToPngConverter;
import com.oakinvest.kiso.core.tool.TextWrapper;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.runtime.StringUtils;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.CANVAS_HEIGHT;
import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.CANVAS_WIDTH;
import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.DESCRIPTION_MAXIMUM_LINES;
import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.DESCRIPTION_MAXIMUM_LINE_LENGTH;
import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.TITLE_MAXIMUM_LINES;
import static com.oakinvest.kiso.cli.util.SocialPreviewConstants.TITLE_MAXIMUM_LINE_LENGTH;
import static com.oakinvest.kiso.cli.util.TemplateConstants.PRECOMPILED_SOCIAL_PREVIEW_TEMPLATE_CLASS;
import static com.oakinvest.kiso.cli.util.TemplateConstants.ROOT_SOURCE_TEMPLATES_DIRECTORY;
import static com.oakinvest.kiso.cli.util.TemplateConstants.SOCIAL_PREVIEW_TEMPLATE_IMAGE;
import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.PNG_EXTENSION;
import static com.oakinvest.kiso.core.util.contants.FileExtensionsConstants.SVG_EXTENSION;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Generates social preview images (SVG and PNG) for pages.
 * Creates Open Graph and Twitter Card preview artifacts.
 */
@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor"})
public final class SocialPreviewImageGenerator {

    /**
     * JTE template engine for rendering SVG.
     */
    private static final TemplateEngine TEMPLATE_ENGINE = createTemplateEngine();

    /**
     * Creates the JTE template engine for SVG rendering.
     *
     * @return JTE template engine
     */
    private static TemplateEngine createTemplateEngine() {
        // If precompiled templates are available, use them ============================================================
        if (precompiledTemplateAvailable()) {
            return TemplateEngine.createPrecompiled(ContentType.Html);
        }

        // If the source templates directory exists, use it ============================================================
        if (Files.isDirectory(ROOT_SOURCE_TEMPLATES_DIRECTORY)) {
            return TemplateEngine.create(new DirectoryCodeResolver(ROOT_SOURCE_TEMPLATES_DIRECTORY), ContentType.Html);
        }

        // If neither precompiled templates nor source templates are available, use the default precompiled templates ==
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    /**
     * Returns true when the JTE precompiled social preview template is available on the classpath.
     *
     * @return {@code true} when the precompiled template is available
     */
    private static boolean precompiledTemplateAvailable() {
        try {
            Class.forName(PRECOMPILED_SOCIAL_PREVIEW_TEMPLATE_CLASS);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    /**
     * Generates social preview images for a page.
     * Creates both SVG and PNG files in the specified output directory.
     *
     * @param siteName        the name of the site
     * @param pageTitle       the page title
     * @param pageDescription the page description
     * @param pageUrl         the canonical URL of the page
     * @param outputDirectory the directory where SVG and PNG files will be written
     * @param filename        the base filename for the generated files (without extension)
     */
    public static void generate(final String siteName,
                                final String pageTitle,
                                final String pageDescription,
                                final String pageUrl,
                                final Path outputDirectory,
                                final String filename) {
        final SocialPreviewImage preview = SocialPreviewImage.builder()
                .siteName(siteName)
                .titleLines(wrapTitle(pageTitle))
                .descriptionLines(wrapDescription(pageDescription))
                .url(pageUrl)
                .build();

        // Filenames.
        final Path svgPath = outputDirectory.resolve(filename + SVG_EXTENSION);
        final Path pngPath = outputDirectory.resolve(filename + PNG_EXTENSION);

        // Image generation.
        generateSvgFile(preview, svgPath);
        generatePngFile(svgPath, pngPath);
    }

    /**
     * Generates the SVG file for the social preview image.
     *
     * @param preview the preview image data
     * @param svgPath the path where the SVG file will be written
     */
    private static void generateSvgFile(final SocialPreviewImage preview, final Path svgPath) {
        try {
            StringOutput svgOutput = new StringOutput();
            TEMPLATE_ENGINE.render(SOCIAL_PREVIEW_TEMPLATE_IMAGE, preview, svgOutput);
            Files.writeString(svgPath, svgOutput.toString().stripLeading(), UTF_8);
        } catch (Exception exception) {
            throw new SocialPreviewGenerationException(
                    "Failed to generate SVG file: " + svgPath,
                    exception
            );
        }
    }

    /**
     * Generates the PNG file by converting the SVG file.
     *
     * @param svgPath the path to the source SVG file
     * @param pngPath the path where the PNG file will be written
     */
    private static void generatePngFile(final Path svgPath, final Path pngPath) {
        if (!SvgToPngConverter.isAvailable()) {
            return;
        }
        SvgToPngConverter.convert(svgPath, pngPath, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    /**
     * Wraps the page title into multiple lines.
     *
     * @param title the page title
     * @return list of wrapped title lines
     */
    private static List<String> wrapTitle(final String title) {
        if (StringUtils.isBlank(title)) {
            return List.of("Untitled");
        }
        return TextWrapper.wrap(title, TITLE_MAXIMUM_LINE_LENGTH, TITLE_MAXIMUM_LINES);
    }

    /**
     * Wraps the page description into multiple lines.
     *
     * @param description the page description
     * @return list of wrapped description lines
     */
    private static List<String> wrapDescription(final String description) {
        if (StringUtils.isBlank(description)) {
            return List.of();
        }
        return TextWrapper.wrap(description, DESCRIPTION_MAXIMUM_LINE_LENGTH, DESCRIPTION_MAXIMUM_LINES);
    }

}
