package com.oakinvest.kiso.core.renderer;

import com.oakinvest.kiso.core.exception.SocialPreviewGenerationException;
import com.oakinvest.kiso.core.renderer.model.SocialPreviewImage;
import com.oakinvest.kiso.core.renderer.util.SvgToPngConverter;
import com.oakinvest.kiso.core.renderer.util.TextWrapper;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.runtime.StringUtils;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.oakinvest.kiso.core.util.SocialPreviewConstants.CANVAS_HEIGHT;
import static com.oakinvest.kiso.core.util.SocialPreviewConstants.CANVAS_WIDTH;
import static com.oakinvest.kiso.core.util.SocialPreviewConstants.DESCRIPTION_MAXIMUM_LINES;
import static com.oakinvest.kiso.core.util.SocialPreviewConstants.DESCRIPTION_MAXIMUM_LINE_LENGTH;
import static com.oakinvest.kiso.core.util.SocialPreviewConstants.TITLE_MAXIMUM_LINES;
import static com.oakinvest.kiso.core.util.SocialPreviewConstants.TITLE_MAXIMUM_LINE_LENGTH;
import static com.oakinvest.kiso.core.util.TemplateConstants.MODULE_SOURCE_TEMPLATES_DIRECTORY;
import static com.oakinvest.kiso.core.util.TemplateConstants.PRECOMPILED_SOCIAL_PREVIEW_TEMPLATE_CLASS;
import static com.oakinvest.kiso.core.util.TemplateConstants.ROOT_SOURCE_TEMPLATES_DIRECTORY;
import static com.oakinvest.kiso.core.util.TemplateConstants.SOCIAL_PREVIEW_TEMPLATE_IMAGE;
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
        if (precompiledTemplateAvailable()) {
            return TemplateEngine.createPrecompiled(ContentType.Html);
        }

        Path sourceTemplatesDirectory = sourceTemplatesDirectory();
        if (sourceTemplatesDirectory != null) {
            return TemplateEngine.create(new DirectoryCodeResolver(sourceTemplatesDirectory), ContentType.Html);
        }

        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    /**
     * Returns true when the JTE precompiled social preview template is available on the classpath.
     *
     * @return true when the precompiled template is available
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
        final Path svgPath = outputDirectory.resolve(filename + ".svg");
        final Path pngPath = outputDirectory.resolve(filename + ".png");

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
