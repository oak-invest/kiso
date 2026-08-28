package com.oakinvest.kiso.cli.command;

import com.oakinvest.kiso.cli.ApplicationVersion;
import com.oakinvest.kiso.cli.configuration.Configuration;
import com.oakinvest.kiso.cli.configuration.ConfigurationLoader;
import com.oakinvest.kiso.cli.exception.ConfigurationLoadingException;
import com.oakinvest.kiso.cli.model.navigation.BundleTree;
import com.oakinvest.kiso.cli.option.DestinationOption;
import com.oakinvest.kiso.cli.option.ProfileOption;
import com.oakinvest.kiso.cli.option.SourceOption;
import com.oakinvest.kiso.cli.publisher.IndexGenerator;
import com.oakinvest.kiso.cli.publisher.LlmsTxtGenerator;
import com.oakinvest.kiso.cli.publisher.SearchIndexGenerator;
import com.oakinvest.kiso.cli.publisher.SitemapXmlGenerator;
import com.oakinvest.kiso.cli.publisher.TagPageGenerator;
import com.oakinvest.kiso.cli.renderer.MarkdownToHtmlRenderer;
import com.oakinvest.kiso.cli.renderer.SocialPreviewImageGenerator;
import com.oakinvest.kiso.cli.tool.IgnorePatternMatcher;
import com.oakinvest.kiso.cli.util.ThemeConstants;
import com.oakinvest.kiso.core.exception.KnowledgeBundleLoadingException;
import com.oakinvest.kiso.core.loader.KnowledgeBundleLoader;
import com.oakinvest.kiso.core.model.bundle.KnowledgeBundle;
import com.oakinvest.kiso.core.validation.ValidationReport;
import com.oakinvest.kiso.core.validation.ValidationRunner;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static com.oakinvest.kiso.core.util.contants.FileConstants.BUNDLE_ZIP_FILENAME;
import static com.oakinvest.kiso.core.util.contants.FileConstants.CONFIGURATION_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.contants.FileConstants.LLMS_TXT_FILENAME;
import static com.oakinvest.kiso.core.util.contants.FileConstants.SEARCH_INDEX_JSON_FILENAME;
import static com.oakinvest.kiso.core.util.contants.FileConstants.SITEMAP_XML_FILENAME;
import static com.oakinvest.kiso.core.util.contants.FileConstants.TAGS_DIRECTORY_NAME;
import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX;

/**
 * Build: Generates a static website from an OKF bundle, placing HTML files alongside the original Markdown files.
 */
@CommandLine.Command(
        name = "build",
        mixinStandardHelpOptions = true,
        description = "Generates a static website from an OKF bundle, placing HTML files alongside the original Markdown files"
)
public class BuildCommand extends AbstractCommand implements Callable<Integer> {

    /** Static assets copied to the generated website root. */
    private static final String[] ASSET_PATHS = {
            "assets/css/application.css",
            "assets/css/daisyui.css",
            "assets/css/themes.css",
            "assets/favicon/kiso_favicon_dark_16x16.svg",
            "assets/favicon/kiso_favicon_dark_32x32.svg",
            "assets/favicon/kiso_favicon_dark_180x180.svg",
            "assets/favicon/kiso_favicon_light_16x16.svg",
            "assets/favicon/kiso_favicon_light_32x32.svg",
            "assets/favicon/kiso_favicon_light_180x180.svg",
            "assets/i18n/en.json",
            "assets/i18n/fr.json",
            "assets/i18n/de.json",
            "assets/i18n/es.json",
            "assets/i18n/it.json",
            "assets/i18n/pt.json",
            "assets/i18n/nl.json",
            "assets/i18n/pl.json",
            "assets/i18n/ru.json",
            "assets/i18n/zh.json",
            "assets/i18n/ja.json",
            "assets/i18n/ko.json",
            "assets/i18n/ar.json",
            "assets/i18n/hi.json",
            "assets/js/browser.js",
            "assets/js/minisearch.js",
            "assets/js/i18next.js",
            "assets/js/kiso-i18n.js",
            "assets/js/kiso-search.js",
            "assets/js/kiso-back-to-top.js"
    };

    /** Source directory. */
    @CommandLine.Mixin
    private final SourceOption sourceOption = new SourceOption();

    /** Destination directory. */
    @CommandLine.Mixin
    private final DestinationOption destinationOption = new DestinationOption();

    /** Profile option. */
    @CommandLine.Mixin
    private final ProfileOption profileOption = new ProfileOption();

    /** Command specification. */
    @SuppressWarnings("unused")
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    /**
     * Get the command specification.
     *
     * @return command specification
     */
    @Override
    protected CommandLine.Model.CommandSpec commandSpec() {
        return commandSpec;
    }

    /**
     * Run the build command.
     */
    @Override
    @SuppressWarnings("checkstyle:MethodLength")
    public Integer call() {
        // A bit of configuration ======================================================================================
        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);

        // Displaying information about the process ====================================================================
        final File sourceDirectory = sourceOption.sourceDirectory().toFile();
        final File destinationDirectory = destinationOption.destinationDirectory().toFile();
        print("Kiso-cli " + ApplicationVersion.get() + " - Running build command");
        print("Sources in " + sourceDirectory.getAbsolutePath());
        print("Building in " + destinationDirectory.getAbsolutePath());
        blankLine();

        try {

            // Loading configuration & profile =========================================================================
            final String profile = profileOption.profile();
            final Configuration configuration = ConfigurationLoader
                    .load(sourceDirectory.toPath(), profile)
                    .orElse(Configuration.empty());

            // Copying OKF bundle files to the destination directory ===================================================
            FileUtils.deleteDirectory(destinationDirectory);
            final IgnorePatternMatcher ignorePatternMatcher = new IgnorePatternMatcher(configuration.content().ignorePatterns());
            final FileFilter fileFilter = file -> {
                Path relativePath = sourceDirectory.toPath().relativize(file.toPath());
                return !ignorePatternMatcher.matches(relativePath);
            };
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory, fileFilter);

            // If a profile is specified, check if the profile exists an index file to use ===========================
            if (StringUtils.isNotBlank(profile)) {
                final File sourceFile = sourceDirectory.toPath()
                        .resolve(CONFIGURATION_DIRECTORY_NAME)
                        .resolve(profile)
                        .resolve(INDEX.getFileName())
                        .toFile();
                if (sourceFile.isFile()) {
                    FileUtils.copyFile(sourceFile, destinationDirectory.toPath().resolve(INDEX.getFileName()).toFile());
                }
            }

            // Loading and checking the bundle =========================================================================
            KnowledgeBundle knowledgeBundle = KnowledgeBundleLoader.load(destinationDirectory.toPath());
            final ValidationReport validationReport = ValidationRunner.runValidation(knowledgeBundle);
            // Print warnings.
            validationReport.warnings().forEach(this::printWarning);
            // Print errors.
            if (validationReport.hasErrors()) {
                validationReport.errors().forEach(this::printError);
                return CommandLine.ExitCode.SOFTWARE;
            }

            // Creating missing index.md files for bundles without index ===============================================
            knowledgeBundle.bundles()
                    .filter(bundle -> !bundle.hasIndexFile())
                    .forEach(bundle -> {
                        try {
                            FileUtils.writeStringToFile(
                                    new File(bundle.absolutePath().toString(), INDEX.getFileName()),
                                    IndexGenerator.generate(bundle),
                                    StandardCharsets.UTF_8
                            );
                            print(INDEX.getFileName() + " generated for " + bundle.absolutePath());
                        } catch (IOException e) {
                            printError("Error generating " + INDEX.getFileName() + " for " + bundle.absolutePath() + ": " + e.getMessage());
                        }
                    });

            // Tags pages generation ===================================================================================
            String tagsDirectory = knowledgeBundle.rootBundle().absolutePath().resolve(TAGS_DIRECTORY_NAME).toString();
            for (String tag : knowledgeBundle.tagSlugs()) {
                try {
                    FileUtils.writeStringToFile(
                            new File(tagsDirectory, tag + ".md"),
                            TagPageGenerator.generate(knowledgeBundle, tag),
                            StandardCharsets.UTF_8
                    );
                    print("Tag page generated for tag: " + tag);
                } catch (IOException e) {
                    printError("Error generating tag page for tag " + tag + ": " + e.getMessage());
                }
            }

            // HTML generation =========================================================================================
            knowledgeBundle = KnowledgeBundleLoader.load(destinationDirectory.toPath(), configuration.site());
            final BundleTree bundleTree = BundleTree.fromBundle(knowledgeBundle.rootBundle());
            knowledgeBundle.bundles()
                    .forEach(bundle -> {

                        // We generate a zip file ======================================================================
                        try (ZipFile zip = new ZipFile(bundle.absolutePath().resolve(BUNDLE_ZIP_FILENAME).toFile())) {
                            zip.addFolder(bundle.absolutePath().toFile(), parameters);
                        } catch (IOException e) {
                            printError("Impossible to generate the zip file for " + bundle.absolutePath() + ": " + e.getMessage());
                        }

                        // We generate the HTML version of every Markdown file in the bundle ===========================
                        bundle.markdownFiles().forEach(markdownFile -> {
                            try {
                                FileUtils.writeStringToFile(
                                        new File(bundle.absolutePath().toString(), markdownFile.htmlFilename()),
                                        MarkdownToHtmlRenderer.render(configuration.site(), configuration.theme(), markdownFile, bundleTree),
                                        StandardCharsets.UTF_8
                                );
                                print("HTML Generated for " + markdownFile.relativePath());
                            } catch (IOException e) {
                                printError("Error generating HTML for " + markdownFile.absolutePath() + ": " + e.getMessage());
                            }

                            // We also generate the social preview images for every Markdown file ======================
                            if (StringUtils.isNotBlank(configuration.site().baseUrl())) {
                                try {
                                    SocialPreviewImageGenerator.generate(
                                            configuration.site().title(),
                                            markdownFile.title(),
                                            markdownFile.description(),
                                            configuration.site().normalizedBaseUrl() + markdownFile.htmlFilePath(),
                                            bundle.absolutePath(),
                                            FilenameUtils.removeExtension(markdownFile.htmlFilename()));
                                } catch (Exception e) {
                                    printError("Error generating social preview image for " + markdownFile.absolutePath() + ": " + e.getMessage());
                                }
                            }
                        });

                    });

            // llms.txt generation =====================================================================================
            FileUtils.writeStringToFile(
                    new File(knowledgeBundle.rootBundle().absolutePath().toString(), LLMS_TXT_FILENAME),
                    LlmsTxtGenerator.generate(knowledgeBundle),
                    StandardCharsets.UTF_8
            );
            print("File " + LLMS_TXT_FILENAME + " generated");

            // sitemap.xml generation ==================================================================================
            FileUtils.writeStringToFile(
                    new File(knowledgeBundle.rootBundle().absolutePath().toString(), SITEMAP_XML_FILENAME),
                    SitemapXmlGenerator.generate(knowledgeBundle),
                    StandardCharsets.UTF_8
            );
            print("File " + SITEMAP_XML_FILENAME + " generated");

            // search-index.json generation ==========================================================================
            FileUtils.writeStringToFile(
                    new File(knowledgeBundle.rootBundle().absolutePath().toString(), SEARCH_INDEX_JSON_FILENAME),
                    SearchIndexGenerator.generate(knowledgeBundle),
                    StandardCharsets.UTF_8
            );
            print("File " + SEARCH_INDEX_JSON_FILENAME + " generated");

            // Theme validation ========================================================================================
            final String theme = configuration.theme().effectiveName();
            if (!ThemeConstants.exists(theme)) {
                printWarning("WARNING: Theme '%s' is not a valid DaisyUI theme.".formatted(theme));
            }

            // Add HTML assets =========================================================================================
            ClassLoader classLoader = MarkdownToHtmlRenderer.class.getClassLoader();
            for (String assetPath : ASSET_PATHS) {
                try (InputStream inputStream = classLoader.getResourceAsStream(assetPath)) {
                    if (inputStream == null) {
                        throw new IOException("Missing asset: " + assetPath);
                    }
                    FileUtils.copyInputStreamToFile(inputStream, new File(destinationDirectory, assetPath));
                }
            }

            // Job done ================================================================================================
            print("Done!");
            return CommandLine.ExitCode.OK;

        } catch (ConfigurationLoadingException e) {
            printError("Error loading configuration: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        } catch (KnowledgeBundleLoadingException e) {
            printError("Error loading knowledge bundle: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

}
